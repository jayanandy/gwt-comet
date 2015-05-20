# Getting Started #

gwt-web-sockets provides a simple GWT wrapper around the browsers native Web Socket implementation.

Currently only Chrome supports this so don't expect it to work in other browsers. This is not (yet) a cross browser Web Socket implementation.

Its simple design is based around GWT's `com.google.gwt.xhr.client.XMLHttpRequest`.

### GWT Module Dependency ###

Download gwt-web-sockets.jar from the [download list](http://code.google.com/p/gwt-comet/downloads/list)

Then you need to include the following GWT module dependency in your project:

```
<inherits name="net.zschech.gwt.websockets.WebSockets" />
```

### Create A Web Socket ###

Create you `WebSocket` using `WebSocket.create(String url)`.

Set your `OpenHandler`, `CloseHandler` and `MessageHandler` for processing Web Socket events.

Use `WebSocket.send(String data)` to send messages.

### Complete Example ###

```
import net.zschech.gwt.websockets.client.CloseHandler;
import net.zschech.gwt.websockets.client.ErrorHandler;
import net.zschech.gwt.websockets.client.MessageEvent;
import net.zschech.gwt.websockets.client.MessageHandler;
import net.zschech.gwt.websockets.client.OpenHandler;
import net.zschech.gwt.websockets.client.WebSocket;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.JavaScriptException;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;

public class WebSocketEntryPoint implements EntryPoint {
	
	private WebSocket webSocket;
	
	private HTML messages;
	private ScrollPanel scrollPanel;
	
	private TextBox url;
	private TextBox input;
	
	private Button connect;
	private Button disconnect;
	private Button send;
	
	@Override
	public void onModuleLoad() {
		
		url = new TextBox();
		url.setValue("ws://websockets.org:8787");
		input = new TextBox();
		input.setValue("Hello World!");
		
		connect = new Button("Connect", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				connect();
			}
		});
		disconnect = new Button("Disconnect", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				webSocket.close();
			}
		});
		send = new Button("Send", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				webSocket.send(input.getValue());
			}
		});
		
		messages = new HTML();
		scrollPanel = new ScrollPanel();
		scrollPanel.setHeight("250px");
		scrollPanel.add(messages);
		
		RootPanel.get().add(scrollPanel);

		FlowPanel controls = new FlowPanel();
		controls.add(url);
		controls.add(connect);
		controls.add(disconnect);
		RootPanel.get().add(controls);
		
		controls = new FlowPanel();
		controls.add(input);
		controls.add(send);
		RootPanel.get().add(controls);
	}
	
	public void connect() {
		try {
			webSocket = WebSocket.create(url.getValue());
			webSocket.setOnOpen(new OpenHandler() {
				@Override
				public void onOpen(WebSocket webSocket) {
					output("open", "silver");
				}
			});
			webSocket.setOnClose(new CloseHandler() {
				@Override
				public void onClose(WebSocket webSocket) {
					output("close", "silver");
				}
			});
			webSocket.setOnError(new ErrorHandler() {
				@Override
				public void onError(WebSocket webSocket) {
					output("error", "red");
				}
			});
			webSocket.setOnMessage(new MessageHandler() {
				@Override
				public void onMessage(WebSocket webSocket, MessageEvent event) {
					output("message: " + event.getData(), "black");
				}
			});
		}
		catch (JavaScriptException e) {
			output(e.toString(), "red");
		}
	}
	
	public void output(String text, String color) {
		DivElement div = Document.get().createDivElement();
		div.setInnerText(text);
		div.setAttribute("style", "color:" + color);
		messages.getElement().appendChild(div);
		scrollPanel.scrollToBottom();
	}
}
```