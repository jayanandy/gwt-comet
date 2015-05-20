# Getting Started #

This getting started guide gives an overview and example code for setting up and processing of Comet messages from the client and server perspective. If you have any questions, thoughts, or ideas send them to the [discussion group](http://groups.google.com/group/gwt-comet).

## Client Side ##

Client side application code makes uses of three main classes:
  1. `CometListener` for processing Comet events.
  1. `CometClient` for starting and stopping the Comet connections.
  1. `CometSerializer` for deserializing GWT serialized types.

### GWT Module Dependency ###

Firstly you need to include the following GWT module dependency in your project:

```
<inherits name="net.zschech.gwt.comet.Comet" />
```

### Cross domain support ###

If you wish to host your GWT application on one domain and connect your Comet client to another domain you need to use GWT's cross site linker by including the following in your project's gwt.xml:

```
<add-linker name="xs"/>
```

At the moment IE only supports cross sub-domains e.g. from `www.example.com` to `comet.example.com`.

### Create Your Comet Listener ###

Next create your `CometListener` for processing your Comet events as follows:

```
CometListener listener = new CometListener() {
	public void onConnected(int heartbeat) {
	}
	public void onDisconnected() {
	}
	public void onHeartbeat() {
	}
	public void onRefresh() {
	}
	public void onError(Throwable exception, boolean connected) {
		// warn the user of the connection error
	}
	public void onMessage(List<? extends Serializable> messages) {
		for (Serializable message : messages) {
			// process your message
		}
	}
};
```

The first five events notify the client application of the state of the Comet connection.
The last `onMessage()` event notifies the client application that a batch of messages have been received.
The messages will be Strings or GWT serialized objects depending on what the server sends.

### Create and Start Comet Client ###

The next step is to create a `CometClient` client and start it as follows:

```
CometClient client = new CometClient(url, listener);
client.start();
```

This will establish a connection to the given `url` and notify the given `listener` of Comet events.

### Receiving GWT Serialized Types ###

You may wish to receive [GWT serialized types](http://code.google.com/webtoolkit/doc/1.6/DevGuideServerCommunication.html#DevGuideSerializableTypes) from your server. To do this you need to create a class extending
`CometSerializer` with the `@SerialTypes` annotation specifying all the types you wish to receive as follows:

```
@SerialTypes({ Message.class, StatusUpdate.class })
public abstract class InstantMessagingCometSerializer extends CometSerializer {}
```

You can then instantiate your `CometSerializer` and associate it with your `CometClient` using as follows:

```
CometSerializer serializer = GWT.create(InstantMessagingCometSerializer.class);
CometClient client = new CometClient(url, serializer, listener);
```

By default GWT's normal RPC serialization is used but you can use GWT 2.0's [direct-eval RPC](http://code.google.com/p/google-web-toolkit/wiki/RpcDirectEval) by specifying the `SerialMode` in the `SerialTypes` annotation for example:

```
@SerialTypes(mode = SerialMode.DE_RPC, value = { Message.class, StatusUpdate.class })
```

## Server Side ##

Server side application code is centred around three main classes:
  1. `CometServlet` extends `HttpServlet` to add Comet processing methods.
  1. `CometServletResponse` similar to `HttpServletResponse` for sending messages to the Comet client and obtaining `CometSession`s.
  1. `CometSession` similar to `HttpSession` for maintaining Comet state across HTTP requests such as message queues.

This gwt-comet library supports two different processing models:
  1. Direct writing of messages to the `HttpServletResponse`s via the `CometServletResponse`s allowing the developer to control message queueing and flow.
  1. Establishing `CometSession` which encapsulates message queueing and writing to the HTTP responses functionality.

### Create Your Own Comet Servlet ###

Firstly you need to create your own Servlet extending `CometServlet` and override the `doComet` method.
When your application returns from the `doComet` the Comet response and HTTP response is suspended so that you can continue to write messages to it asynchronously from other threads. To terminate the Comet response and close the HTTP response call `CometResponse.terminate()`.
The application can be notified of the termination of the response by overriding the `cometTerminated` method.

### Direct Model ###

If you wish to control your own message queueing and flow control

```
public class InstantMessagingServlet extends CometServlet {
	
	private ApplicationInstantMessagingSystem messagingSystem = ...;
	
	@Override
	protected void doComet(CometServletResponse cometResponse) throws ServletException, IOException {
		cometResponse.write(messagingSystem.getQueuedMessages());
		messagingSystem.registerMessageSink(cometResponse);
	}
	
	@Override
	public void cometTerminated(CometServletResponse cometResponse, boolean serverInitiated) {
		messagingSystem.unregisterMessageSink(cometResponse);
	}
}
```

### Comet Session Model ###

If you wish to use the built in message queueing you can use the `CometSession`. If you wish to setup the `CometSession` when the first request is received:

```
public class InstantMessagingServlet extends CometServlet {
	
	private ApplicationInstantMessagingSystem messagingSystem = ...;
	
	@Override
	protected void doComet(CometServletResponse cometResponse) throws ServletException, IOException {
		CometSession cometSession = cometResponse.getSession(false);
		if (cometSession == null) {
			// The comet session has not been created yet so create it.
			cometSession = cometResponse.getSession();
			messagingSystem.registerMessageSink(cometSession);
		}
	}
}
```

If you wish to be notified when the `CometSession` is created or destroyed you can implement a `HttpSessionBindingListener` and listen for `valueBound` and `valueUnbound` events with the `HttpSessionBindingEvent.getName()` being `CometSession.HTTP_SESSION_KEY` for example:

```
public class InstantMessagingCometSessionListener implements HttpSessionBindingListener {
	
	private ApplicationInstantMessagingSystem messagingSystem = ...;
	
	@Override
	public void valueBound(HttpSessionBindingEvent event) {
		if (event.getName().equals(CometSession.HTTP_SESSION_KEY)) {
			CometSession session = (CometSession) event.getValue();
			messagingSystem.registerMessageSink(cometSession);
		}
	}
	
	@Override
	public void valueUnbound(HttpSessionBindingEvent event) {
		if (event.getName().equals(CometSession.HTTP_SESSION_KEY)) {
			CometSession session = (CometSession) event.getValue();
			messagingSystem.unregisterMessageSink(cometSession);
		}
	}
}
```

If you wish to setup the `CometSession` when the HTTP session created you can implement a `HttpSessionListener` and listen for `sessionCreated` and `sessionDestroyed` events with then use the static `CometServlet.getCometSession` method to obtain a Comet session for example:

```
public class InstantMessagingHttpSessionListener implements HttpSessionListener {
	
    private ApplicationInstantMessagingSystem messagingSystem = ...;
    
	@Override
	public void sessionCreated(HttpSessionEvent event) {
		CometSession cometSession = CometServlet.getCometSession(event.getSession());
		messagingSystem.registerMessageSink(cometSession);
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent event) {
		CometSession cometSession = CometServlet.getCometSession(event.getSession());
		messagingSystem.unregisterMessageSink(cometSession);
	}
}
```