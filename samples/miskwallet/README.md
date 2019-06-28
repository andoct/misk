# MiskWallet 
This is a sample app that makes use of MISK. All commands below assume you're in the root directory 
of this repository.

## Building
Build miskwallet:

```
  $ ./gradlew clean assemble
```  

## Docker
### Building
Build a Docker image of MiskWallet:

```
  $ docker build -t miskwallet-image miskwallet/
```

### Running locally
Visit [Docker for Mac](https://docs.docker.com/docker-for-mac/install/) to install Docker on a Mac for testing.

Run miskwallet in Docker locally:
```
  $ docker run -p 8080:8080 miskwallet-image
```

Test the [HelloWebAction](http://localhost:8080/hello/world) in a browser.

### Running on GCP

#### Tag your image and push to GCR
```
  $ docker tag misk-miskwallet:0.0.1 gcr.io/${YOUR_GCP_PROJECT}/misk-miskwallet:0.0.1
  $ gcloud docker -- push gcr.io/${YOUR_GCP_PROJECT}/misk-miskwallet:0.0.1
```

#### Start a Kubernetes Cluster (if you don't already have one)
Browse to [https://console.cloud.google.com/kubernetes/list](https://console.cloud.google.com/kubernetes/list)

Make sure you select your appropriate project, if it isn't already pre-selected for you. Connect to 
your cluster once it's ready, and connect to the Kubernetes Web UI.

#### Deploy MiskWallet
* Use `gcr.io/${YOUR_GCP_PROJECT}/misk-miskwallet:0.0.1` as your Container Image
* Specify this to be an EXTERNAL service
* Map port 8080 on your container to a target port of 80

Once deployed, you should see an external IP address for your service. You should be able to browse 
to this.
