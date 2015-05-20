# A Complete Chat Example #

This complete example includes all client and server side code required to implement a simple group chat system. See the complete [source code](http://code.google.com/p/gwt-comet/source/browse/#svn/trunk/src/net/zschech/gwt/chat).

## Client Side ##

The [client package](http://code.google.com/p/gwt-comet/source/browse/#svn/trunk/src/net/zschech/gwt/chat/client) includes:
  1. A [chat entry point](http://code.google.com/p/gwt-comet/source/browse/trunk/src/net/zschech/gwt/chat/client/ChatEntryPoint.java) which sets up a simple gui for logging in, setting your status, and sending messages.
  1. A [chat remote service](http://code.google.com/p/gwt-comet/source/browse/trunk/src/net/zschech/gwt/chat/client/ChatService.java) and associated message classes etc which the client uses to interact with the server.

## Server Side ##

The [server package](http://code.google.com/p/gwt-comet/source/browse/#svn/trunk/src/net/zschech/gwt/chat/server) includes:
  1. An implementation of the [chat remote service](http://code.google.com/p/gwt-comet/source/browse/trunk/src/net/zschech/gwt/chat/server/ChatServiceImpl.java).
  1. The map of logged in users used for message routing.

### web.xml ###

```
<web-app>
  <!-- Listener for shutting down the comet processor when the ServletContext is destroyed -->
  <listener>
    <listener-class>net.zschech.gwt.comet.server.CometServletContextListener</listener-class>
  </listener>
  
  <!-- Listener for invalidating CometSessions when HTTPSessions are invalidated -->
  <listener>
    <listener-class>net.zschech.gwt.comet.server.CometHttpSessionListener</listener-class>
  </listener>

  <!-- the RPC service called by the client -->
  <servlet>
    <servlet-name>chatService</servlet-name>
    <servlet-class>net.zschech.gwt.chat.server.ChatServiceImpl</servlet-class>
  </servlet>

  <!-- the comet servlet for streaming messages to the client -->
  <servlet>
    <servlet-name>chatComet</servlet-name>
    <servlet-class>net.zschech.gwt.comet.server.CometServlet</servlet-class>
  </servlet>

  <servlet-mapping>
    <servlet-name>chatService</servlet-name>
    <url-pattern>/net.zschech.gwt.chat.Chat/chat</url-pattern>
  </servlet-mapping>
  <servlet-mapping>
    <servlet-name>chatComet</servlet-name>
    <url-pattern>/net.zschech.gwt.chat.Chat/comet</url-pattern>
  </servlet-mapping>
</web-app>
```