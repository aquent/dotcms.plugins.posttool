Post Viewtool
=================================================
A plugin for dotCMS that adds a viewtool that allows the front-end developer to make simple post requests like curl.
The plugin now supports The following HTTP Method Types:
* POST
* PUT
* GET
* HEAD
* DELETE
You can also pass a content-type to the methods to control what content type is used for the request.
Added some new methods that allow you to POST or PUT xml or json data
Now supports https requests (you might need to add javax.net to your exported packages for this to work)

Installation
-----
* Navigate to the dotCMS Dynamic plugins page: "System" > "Dynamic Plugins"
* Click on "Upload plugin" and select the .jar file located in the "build/jar/" folder

Compatibility
-----
DotCMS 3.1+
For 2.3x+ compatibility see previous releases.

Usage
-----
```
#set($url       = "http://${request.getServerName()}/test/viewtools/posttool2.htm")
#set($params    = "postdata=hello 褩䤩矩い覦")
#set($paramsMap = $contents.getEmptyMap())
#set($_dummy    = $paramsMap.put("postdata","hello 褩䤩矩い覦"))

<h3>Test GET:</h3>
#set($resp      = $post.sendGet("${url}?${params}"))
<p>Status:  $resp.getResponseCode() <br />
You got: <textarea>$resp.getResponse()</textarea> </p>

<h3>Test POST:</h3>
#set($resp2     = $post.send($url, $params, "POST"))
<p>Status:  $resp2.getResponseCode() <br />
You got: <textarea>$resp2.getResponse()</textarea> </p>

<h3>Test PUT with a Map and Content Type:</h3>
#set($resp3     = $post.sendPut($url, $paramsMap, "application/octet-stream"))
<p>Status:  $resp3.getResponseCode() <br />
You got: <textarea>$resp3.getResponse()</textarea> </p>

<h3>Test POST with JSON Data:</h3>
#set($jsonData  = '{"title":"foo","body":"bar","userId":1}')
#set($resp4     = $post.postStringData("http://jsonplaceholder.typicode.com/posts", $jsonData, "application/json"))
<p>Status:  $resp4.getResponseCode() <br />
You got: <textarea>$resp4.getResponse()</textarea> </p>
```

Building
--------
* Install Gradle (if not already installed)
* gradle jar 