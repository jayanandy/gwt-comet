# Getting Started #

gwt-event-source provides a simple GWT wrapper around the browsers native Event Source implementation.

Currently only Safari and Chrome support this so don't expect it to work in other browsers.

Its simple design is based around GWT's `com.google.gwt.xhr.client.XMLHttpRequest`.

### GWT Module Dependency ###

Download gwt-event-source.jar from the [download list](http://code.google.com/p/gwt-comet/downloads/list)

Then you need to include the following GWT module dependency in your project:

```
<inherits name="net.zschech.gwt.eventsource.EventSource" />
```

### Create A Event Source ###

Create you `EventSource` using `EventSource.create(String url)`.

Set your `OpenHandler`, `ErrorHandler` and `MessageHandler` for processing Event Source events.