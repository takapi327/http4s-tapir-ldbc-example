ThisBuild / resolvers += "Lepus Maven" at "s3://com.github.takapi327.s3-ap-northeast-1.amazonaws.com/ldbc/"
addSbtPlugin("com.github.takapi327" % "ldbc-plugin" % "0.0.0+1487-d21b02cc+20231206-2009-SNAPSHOT")
addSbtPlugin("com.typesafe.sbt"  % "sbt-native-packager" % "1.7.6")
addSbtPlugin("com.mintbeans" % "sbt-ecr" % "0.15.0")
addSbtPlugin("com.github.sbt" % "sbt-release" % "1.1.0")
addSbtPlugin("com.frugalmechanic" % "fm-sbt-s3-resolver" % "0.20.0")
