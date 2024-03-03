# Adlatus GUI

Adlatus GUI is a Nx monorepo that houses multiple GUI applications, each serving a distinct purpose within the Adlatus
system.

## Applications

### 1. Party Management Application

The Party Management Application is designed to handle and manipulate data related to "Parties" within the Adlatus
system. This application allows users to manage and organize information about parties involved in various processes.

### 2. Resource Catalog Application

The Resource Catalog Application provides a platform for managing the catalog of resources in the Adlatus system. Users
can add, modify, and organize resources efficiently using this application.

### 3. Agreement Application

The Agreement Application is a tool for managing and manipulating agreements within the Adlatus system. Users can
create, edit, and oversee various types of agreements.

## Building apps, building image

### Development

Install Angular CLI globally

```shell
npm i @angular/cli -g
```

Run application in hot-reload

```shell
nx serve application-name
```

Example: Terminal/Console: nx serve agreement-management. Others apps is in apps folder.

Error: nx : File C:\Users\...\adlatus-gui\node_modules\.bin\nx.ps1 cannot be loaded because running scripts is disabled
on this system. For more information, see about_Execution_Policies at https:/go.microsoft.com/fwlink/?LinkID=135170.
Windows PoweShell:

```shell
Set-ExecutionPolicy RemoteSigned -Scope CurrentUser
```

For easier, faster development install NX IntelliJ plugin.

### 1. Building apps

To build all apps in the monorepo, run the following command `from project root`:

```shell
node scripts/build-all-apps.js
```

### 2. Building image

To then build the docker image, run the following command `from project root`:

```shell
docker build -t adlatus-gui -f ci/Dockerfile .
```

## Running Adlatus GUI

To run the docker image, run the following command:

```shell
docker run -p <host_port>:80 adlatus-gui
```

for example:

```shell
docker run -p 8080:80 adlatus-gui
```

Party Management can be accessed at `http://localhost:8080/party`

Resource Catalog Management can be accessed at `http://localhost:8080/catalog`

Agreement Management can be accessed at `http://localhost:8080/agreement`

Order Management can be accessed at `http://localhost:8080/order`

Reporting System can be accessed at `http://localhost:8080/reporting`


