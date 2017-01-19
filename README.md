# tweeeter
tweeeter is a demo application that collects statistics from the Twitter streaming api and outputs them to an EventSource

### Quick Start (requires SBT)
- Clone the project
- With your twitter API details, set the `CONSUMER_KEY`, `CONSUMER_SECRET`, `TOKEN` and `TOKEN_SECRET` system properties or override them in [application.conf](core/src/main/resources/application.conf). 
- Enter `sbt core/run` 
- Navigate to [http://localhost:9000](http://localhost:9000) with an [EventSource compatible browser](http://caniuse.com/#feat=eventsource).

You should see something like this

![alt text](/static/example.png "Example")

### Walkthrough
  - [TweeeterApp](core/src/main/scala/com/brianmowen/tweeeter/TweeeterApp.scala) - Entry Point to the application. 
  - [TweetStatModel](core/src/main/scala/com/brianmowen/tweeeter/TweetStatModel.scala) - A simple model of the statistics to collect. 
  - [TweeeterProtocol](core/src/main/scala/com/brianmowen/tweeeter/TweeeterProtocol.scala) - JSON marshalling using [spray-json](https://github.com/spray/spray-json). 
  - [TweeeterSettings](core/src/main/scala/com/brianmowen/tweeeter/TweeeterApp.scala) - Settings loaded from [application.conf](core/src/main/resources/application.conf). 
  - [Client](core/src/main/scala/com/brianmowen/tweeeter/client/Client.scala) - A simple *akka-http* client with mixed in request handlers. 
  - [GetPublicStreamRequest](core/src/main/scala/com/brianmowen/tweeeter/client/GetPublicStreamRequest.scala) - The functions that connect to the Twitter API, unmarshall the response, and handle errors. 
  - [OauthSigner](core/src/main/scala/com/brianmowen/tweeeter/helpers/OauthSigner.scala) - Homemade OAuth 1.0 request signer for *akka-http* that I wrote for this demo. I've only used this with the public stream requests so I can't guarantee it would work for other requests (with query or body params). 
  - [WorkerPool](core/src/main/scala/com/brianmowen/tweeeter/helpers/WorkerPool.scala) - An `akka-streams` flow that parallelizes requests that run through it. 
  - [SimpleEmojiParser](core/src/main/scala/com/brianmowen/tweeeter/helpers/SimpleEmojiParser.scala) - This subclasses the emoji parser from [emoji-java](https://github.com/vdurmont/emoji-java) and is used to extract emoji from a string. 
  - [TweetStats](core/src/main/scala/com/brianmowen/tweeeter/logic/TweetStats.scala) - Provides a `Stat` type class an a default implementation for `TweetStatModel`. The methods on this type class allow tweets to be processed in parallel.
  - [Semigroups](core/src/main/scala/com/brianmowen/tweeeter/logic/Semigroups.scala) - Provides a `Semigroup` implementation for `TweetStatModel` so that parallelized results can be combined. 
  - [Renderable](core/src/main/scala/com/brianmowen/tweeeter/logic/Renderable.scala) - Type class that allows arbitrary rendering, along with default implentations for rendering `TweetStatModel` to `Map` and `String`.  
  - [TweetAccumulator](core/src/main/scala/com/brianmowen/tweeeter/logic/TweetAccumulator.scala) - A simple flow that takes a stream of tweets, parallelizes them, merges and accumulates the output. 
  - [Server](core/src/main/scala/com/brianmowen/tweeeter/Server.scala) - This is the *akka-http* server that returns the HTML page and SSE endpoint. 

