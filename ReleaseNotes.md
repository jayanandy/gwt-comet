# Release Notes #

## 1.2.3 ##

  * Fixed up licensing: [issue 18](https://code.google.com/p/gwt-comet/issues/detail?id=18).
  * Update to GWT 2.3 serializers: [issue 19](https://code.google.com/p/gwt-comet/issues/detail?id=19).

## 1.2.2 ##

  * Flow control for reducing send rate if the client is struggling to processes the messages in a timely manner.
  * Update to GWT 2.2 serializers: [issue 13](https://code.google.com/p/gwt-comet/issues/detail?id=13) and [issue 14](https://code.google.com/p/gwt-comet/issues/detail?id=14).

## 1.2.1 ##

  * Fix [issue 9](https://code.google.com/p/gwt-comet/issues/detail?id=9) where serialization code changed between gwt-2.1.0.M3 and gwt-2.1.0

## 1.2.0 ##

  * Update to GWT 2.1 as per [discussion](http://groups.google.com/group/gwt-comet/browse_thread/thread/d5da4e38160e2927).

## 1.1.6 ##

  * Support for HttpServletResponseWrappers as per [discussion](http://groups.google.com/group/gwt-comet/browse_thread/thread/d81d0d3fb7b96a69).
  * Support for [Server-Sent Events](http://dev.w3.org/html5/eventsource/). Safari and Chrome currently implement this.
  * Various performance improvements.
  * Prevent Firefox from aborting XHR request with ESC key as per [discussion](http://groups.google.com/group/gwt-comet/browse_thread/thread/daba4abcd240fa6c).
  * Fix bug in client requesting heartbeat interval with a url parameter as per [discussion](http://groups.google.com/group/gwt-comet/browse_thread/thread/d236d0ccf453f5a5).

## 1.1.5 ##

  * Support for embedded Grizzly and GlassFish Async Servlets which implement non-blocking IO and non-blocking threading.
  * Prevent Chrome sniffing mime types removing the need for the server to padding [r138](http://code.google.com/p/gwt-comet/source/detail?r=138). Chrome 6 increased the previously required padding preventing gwt-comet for detecting connections. Any changes to Chrome in the future will not require changes to gwt-comet now.
  * Fixed client reconnection logic.
  * Improved server side error handling.

## 1.1.4 ##

  * Fix [issue 1](https://code.google.com/p/gwt-comet/issues/detail?id=1)
  * Rewritten test example.
  * Chat and Test examples packaged into a war file.
  * Fixed refresh logic when no sessions as per [discussion](http://groups.google.com/group/gwt-comet/browse_thread/thread/c7125628a8d867a9).

## 1.1.3 ##

  * Added support for updating Jetty 7's sessions last access time with out refreshing the connection.
  * Support for IE client side message batch processing. (Changes the IE message protocol)
  * Support for using arrays as a serialized message type. (requires two issues to be fixes in GWT [1922](http://code.google.com/p/google-web-toolkit/issues/detail?id=1822) and [4657](http://code.google.com/p/google-web-toolkit/issues/detail?id=4657))
  * Removed System.out debugging [issue 1](https://code.google.com/p/gwt-comet/issues/detail?id=1)
  * Improved specific server fall back code.
  * Fixed session creation logic [r99](https://code.google.com/p/gwt-comet/source/detail?r=99)
  * Improved session creation logic [r100](https://code.google.com/p/gwt-comet/source/detail?r=100)

## 1.1.2 ##

  * Fixed "Unexpected disconnection" error when disconnecting `HTTPRequestCometTransport` from the client side.

## 1.1.1 ##

  * Support for connecting across sub domains in all browsers.
  * Support for connecting across all domains in all browsers except IE.

## 1.1.0 ##

  * Support for GWT 2.0. Not compatible with GWT 1.7 or earlier.
  * Support for [direct-eval RPC](http://code.google.com/p/google-web-toolkit/wiki/RpcDirectEval). see [Receiving GWT Serialized Types](GettingStarted#Receiving_GWT_Serialized_Types.md)

## 1.0.12 ##

  * Added support for updating Catalina/Tomcat 5.5's sessions last access time with out refreshing the connection.

## 1.0.11 ##

  * Added support for updating Catalina/Tomcat 6.0's sessions last access time with out refreshing the connection.
  * Cleaned up the Comet test cases.
  * Added support for overlapping requests to mask connection refresh establishment latency.

## 1.0.10 ##

  * Fixed an issue where the deflater was not flushing everything for large chunks of data.
  * Improved the performance of compression by not computing Adler check sum. IE does not support these anyway.
  * Named the heartbeat thread for easier diagnosis.
  * Cleaned up the Comet test cases.

## 1.0.9 ##

  * Added support for keeping the HTTP session alive when only the comet connection is in use and no other client HTTP requests are being made which would normally cause the HTTP session to timeout. [r28](http://code.google.com/p/gwt-comet/source/detail?r=28) [r29](http://code.google.com/p/gwt-comet/source/detail?r=29)

## 1.0.8 ##

  * Fixed an issue with `HTTPRequestCometTransport`'s handling of server side errors. [r23](http://code.google.com/p/gwt-comet/source/detail?r=23)
  * Made a change to ensure there is only one active `CometResponse` per `CometSession` [r24](http://code.google.com/p/gwt-comet/source/detail?r=24)