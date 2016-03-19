name := "JavaESClient"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "net.liftweb" 			     %% 	"lift-json"		        %   "2.6",
  "com.typesafe"          	 %      "config"                %   "1.2.1",
  "org.elasticsearch"         %    "elasticsearch"         % "2.2.0",
  "ch.qos.logback"             %      "logback-classic"     	%   "1.0.13",
  "org.elasticsearch.plugin" % "delete-by-query" % "2.2.0",
  "org.scalatest"              %%     "scalatest"    	        %   "2.2.2"      %     "test"
)

parallelExecution in Test := false
