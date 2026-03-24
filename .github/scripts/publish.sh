getProp(){
   grep "${1}" gradle.properties | cut -d'=' -f2 | sed 's/\r//'
}
publishVersion=$(getProp VERSION_NAME)
snapshotSuffix='SNAPSHOT'

chmod +x ./gradlew

if [[ "$publishVersion" != *"$snapshotSuffix"* ]]; then
  echo "auto release artifacts of ${publishVersion}"
  ./gradlew publishAndReleaseToMavenCentral
else
  echo "public artifacts of ${publishVersion}"
  ./gradlew publishAllPublicationsToMavenCentral    
fi