Post Viewtool
=================================================
A plugin for dotCMS that adds a viewtool that allows the front-end developer to make simple post requests like curl

Installation
-----
* Navigate to the dotCMS Dynamic plugins page: "System" > "Dynamic Plugins"
* Click on "Upload plugin" and select the .jar file located in the "build/jar/" folder

Compatibility
-----
Known to work with 2.5.1. Should be fully 2.3+ compatible though.

Usage
-----
```
#set($url = "http://${request.getServerName()}/test/viewtools/posttool2.htm")
#set($params = "postdata=hello 褩䤩矩い覦")

#set($paramsMap = $contents.getEmptyMap())
#set($_dummy = $paramsMap.put("postdata","hello 褩䤩矩い覦"))

<h3> Get:  </h3>
#set($resp = $post.sendGet("${url}?${params}"))
<p>Status:  $resp.getResponseCode() <br /> 
You got: <textarea>$resp.getResponse()</textarea> </p>


<h3> Post: </h3>
#set($resp2 = $post.send($url, $params, "POST"))
<p>Status:  $resp2.getResponseCode() <br /> 
You got: <textarea>$resp2.getResponse()</textarea> </p>


<h3> Test with a Map </h3>
#set($resp3 = $post.send($url, $paramsMap, "GET"))
<p>Status:  $resp3.getResponseCode() <br /> 
You got: <textarea>$resp3.getResponse()</textarea> </p>
```

Building
--------
* Install Gradle (if not already installed)
* gradle jar 