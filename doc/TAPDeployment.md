# Tanzu Application Platform (TAP) Deployment

Tanzu Application Platform is a viable deployment platform for Hungryman, and there are various eventing options for the deployment.

* Using RabbitMQ for all asynchronous messaging.
* Using Knative eventing for event orchestration.

The simplest option is to use RabbitMQ; however, using KNative eventing provides for extended capabilities such as scale to 0 and auto scaling.  In both options, a Spring Cloud Streams binding implementation is required for moving messages from the `hungryman-search` applications; RabbitMQ is the default binding provided.  Neither option requires a change in source code, however different runtime dependencies are configured a build time depending on which eventing implementation is desired.  The `main` branch of this repository uses the RabbitMQ implementation, and the `kneventing` branch contains optional dependency configurations for services that will consume message via CloudEvents.

##Prerequisites

These instructions assume that you have a TAP cluster up and running with the following packages installed:

* Tanzu Build Services
* Tanzu Cloud Native Runtimes
* Tanzu Service Bindings
* Tanzu Services Toolkit
* Tanzu Out of the Box Supply Chains
* Tanzu Out of the Box Templates
* Tanzu Source Controller

## RabbitMQ Installation

A Spring Cloud Streams binding is a required dependency for Hungryman, and RabbitMQ is the default binding.  Although Hungryman doesn't care where or how your RabbitMQ cluster is deployed, there are relatively simple out of the box solutions, and this document will cover using a Kubernetes operator for Rabbit MQ.

### RabbitMQ Operator

The RabbitMQ Kubernetes operator provides a resource based option for deploying clusters to a Kubernetes cluster.  It also has the nicety of deployed clusters supporting the Kubernetes [Service Binding Spec](https://github.com/servicebinding/spec).  To install the RabbitMQ operator, run the following command against your cluster.  Later instructions that use service binding will assume that your RabbitMQ cluster and the Hungryman application are installed on the same cluster for the purpose of service binding, but this is not technically a requirement.

```
kubectl apply -f "https://github.com/rabbitmq/cluster-operator/releases/latest/download/cluster-operator.yml"
```

If successfully installed, there will be an RabbitMQ cluster operator pod running in the `rabbitmq-system` namespace.

### RabbitMQ Topology Operator

If you choose to use the KNative eventing deployment option, you will also need to deploy the RabbitMQ Topology Operator.  This operator allows for the declarative creation of resources like RabbitMQ exchanges, queues, and bindings.  The topology operator is a dependency of the KNative RabbitMQ Source resource which will be covered later. 

To install the RabbitMQ Topology operator, run the following command against your cluster. 

```
kubectl apply -f "https://github.com/rabbitmq/messaging-topology-operator/releases/latest/download/messaging-topology-operator-with-certmanager.yaml"
```

### RabbitMQ Eventing Source

If you choose to use the KNative eventing deployment option, you also need to deploy the KNatvie RabbitMQ Eventing Source resources.  The eventing source acts as a bridge between messages emitted by the `hungryman-search` application the rest of the downstream services.

*NOTE:* The RabbitMQ Eventing source is pre-installed into your TAP deployment if you have chosen to deploy the Cloud Native Runtimes package.  However; versions 1.1.x and below do have an up to date version that will work with some of the declared resources in these instructions.  These instruction require RabbitMQ Eventing 1.4.0 or later.


## MySQL Installation

By default Hungryman services will use an in memory database.  However; the contents of the database will be reset if a pod is restarted or a new revision of the application is deployed.  The default application build supports MySQL as a database target.  Similar to RabbitMQ, MySQL can be deployed in various ways and the services don't care where or how MySQL was installed.  Using an a MySQL Kubernetes operator that supports the service binding specification is an optimal option for application deployment.  The Tanzu MySQL for Kubernetes package is one possibility that supports this option, and installation instructions can be found [here](https://docs.vmware.com/en/VMware-Tanzu-SQL-with-MySQL-for-Kubernetes/1.4/tanzu-mysql-k8s/GUID-install-operator.html).


## Service Provisioning and Binding

With the necessary operators installed, instances of MySQL and RabbitMQ can be spun up by applying the appropriate resources into your cluster.  Additionally, resource claims can be created that allow the application services to bind to and consume the MySQL and RabbitMQ instances.

Run the following command in the ./deployment/tap to deploy instances of RabbitMQ and MySQL and create the resources for service binding.  A biproduct of this command will also make your services and service instances visible to the `tanzu cli`.

```
./provision.sh
```

You will be asked for the namespace where you want your service instances to be deployed and the namespace where you will be deploying the Hungryman application.

## Application Deployment

Now that service instances of RabbitMQ and MySQL are deployed, you are now ready to deploy the Hungyman application.  The deployment process will flow through the TAP build services which means workload resource object will be created to kick off the build and deployment of the application into your cluster.  

The previous step (service provisioning) outputs a `workloads.yaml` file that contains the necessary workload configuration to apply to your cluster.  Run the following command to kick off the build and deployment process.  

```
kubectl -f workloads.yaml
```