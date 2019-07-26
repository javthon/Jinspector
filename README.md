

![build](https://img.shields.io/badge/build-passing-brightgreen.svg)
![license](https://img.shields.io/badge/license-MIT-blue.svg)
![codecov](https://img.shields.io/badge/codecov-73%25-orange.svg)
![jdk](https://img.shields.io/badge/jdk-1.8%2B-blue.svg)
![platform](https://img.shields.io/badge/platform-windows%7Clinux-lightgrey.svg)
![language](https://img.shields.io/badge/language-java-yellow.svg)


## What is it?
Jinspector is an easy-to-use monitoring tool for Linux and Windows operating systems. Jinspector is based on SpringBoot, its main function is to offer an easy flexible way to monitor your services status on distributed systems. Jinspector does not provide monitoring metrics like network utilization, CPU load or disk space consumption. It just monitor whether your services are working or not and send an email report. 

## Key features
- Easy to install
- Simple configuration
- Cross platform
- Report friendly
- Flexible inspection
- No need to restart after modifying configuration

## Who is suitable to use it
- Do issue inspections only
- The number of hosts is small
- Want to install and configure in an easy way


## Contents
- [Requirements](#requirements)
- [Get started](#get-started)
- [Get started with docker](#get-started-with-docker)
- [Configuration](#configuration)
- [Tips](#tips)
- [TODO](#todo)
- [Contributing](#contributing)


## Requirements
- Jinspector requires JDK1.8+ installed  
- You need to know about yaml language, if you don't know this language, it doesn't matter, cause it's quite simple. You don't need to know all about this language, just follow the configuration sample and you will know how to configure it easily   


## Get Started
Step 1: download the latest release  
Step 2: unzip the release file  
Step 3: find the `config` folder, configure the files inside according to the [configuration](#configuration) rules below  
Step 4: go to the jinspector folder and run the monitoring progarm, for Linux use `nohup java -jar jinspector.jar &`, for windows use `java -jar jinspector.jar`  


## Get started with docker
This part needs to be accomplished


## Configuration

configuration files:
- [id.yml](#idyml)
- [server.yml](#serveryml)
- [expose.yml](#exposeyml)
- [local.yml](#localyml)
- [schdule.yml](#schduleyml)

These files can be found in the latest release

##### id.yml
This file is simple, it just used to identify your host, here is the example file:
```
id: anyid
```

##### server.yml
This file describes which host will be chosen as a server and which port will the server use. In some cases a client can't send out inspection results because it has shut down due to unexpected reasons, so a list of required host ids are necessary, they can help you know whether the hosts' clients are working
```
server:
  #server id
  id: anyid
  #server ip
  ip: 108.61.174.70
  #Jinspector server will use this port
  port: 2333
  #unique string that identify your clients
  token: zxjawdjawdjgawgnnxuw

requiredHostIds:
  #sometimes a client can't send out inspection results because it has shut down due to
  #unexpected reasons, so a list of required host ids are necessary, they can help you know
  #whether the hosts' clients are working
  #Here are the list of host ids below, host id is configured at id.yml
  - anyid
  - clientid1
  - clientid2


```

##### expose.yml
We can judge whether a service is running on a remote host mostly by its port, if the port is listening that will prove the service is running, for web aplications an accessible url tells the service works fine, all configurations in expose.yml can be access from outsite, sample configuration is as below:
```
# all name and description attributes are not required
connections:
  web:
    # full attributes edition
    - connection:
        name: ASF homepage
        #(required) the url you want to check if it is accessible
        url: http://www.apache.org/
        description: The Apache Software Foundation website
    # simplest attributes edition
    - connection:
        url: https://mvnrepository.com/
    - connection:
        name: unreachable site
        url: https://www.unreachable.com

  ports:
    # full attributes edition
    - connection:
        name: mysql
        #(required)
        host: nanmatou.club
        #(required)
        port: 3306
        description: mysql port
    # simplest attributes edition
    - connection:
        host: nanmatou.club
        port: 3319
```
##### local.yml
Some services can't access from outside, the hosts have only private network environment, or like a timed task, it doesn't need to use a port, in this circumstance expose.yml won't work, so we need to configure local inspection, the configuration sample file is shown as below:
```
# all name and description attributes are not required
servers:
  - server:
      # the id is related to the id in id.yml
      id: serverhostid
      name: master
      services:
        # full attributes edition
        - service:
            name: Tomcat
            inspectCommand: jps
            expectedResult: Bootstrap
            description: Is tomcat running?
        # simplest attributes edition
        - service:
            inspectCommand: sudo docker ps
            expectedResult: mysql
        - service:
            inspectCommand: ps -e
            expectedResult: unknow process
  - server:
      id: clienthostid
      name: client1
      services:
        - service:
            inspectCommand: tasklist
            expectedResult: chrome
        - service:
            inspectCommand: netstat -ano
            expectedResult: 49664
```
The local inspections are made up of a group of severs, each server has an `id`, which is configured at `id.yml`, and `name` is used to let you know which host it is, it will be displayed in an email report. Each server has several `services`, each `service` defines how you inpect the service and whether the service is running.For example, I want to know whether my docker mysql container is running on linux, so I logined to my linux server and typed the command "docker ps" and I saw this:
```
CONTAINER ID     IMAGE     COMMAND     CREATED     STATUS     PORTS     NAMES
b7dea5675023 mysql:5.7 "docker-entrypoint.s…" 5 weeks ago Up 5 weeks 33060/tcp, 0.0.0.0:3318->3306/tcp mysql-service
```
So if I see `mysql-service`, the docker mysql container is running, the configuration block is like below:
```
- service:
    name: docker mysql
    inspectCommand: docker ps
    expectedResult: mysql-service
    description: Whether the docker mysql container running
```

##### shedule.yml
This file defines the email `sender` and inspection `shedule`, Jinspector has a built-in email sender, if you want to set up your own email config, please visit your mail service provider website for help, for gmail, check [sending email via gmail](https://www.mkyong.com/java/javamail-api-sending-email-via-gmail-smtp-example/)
`receivers` defines a list of email address you want to send to
in `schedule` node, `report` will send mail with all inspection results while `inspect` only send results with issues
```
mail:
  sender:
    host: smtp.qq.com
    port: 465
    address: jinspector@foxmail.com
    password: yjrvxfydupwkdfch
    nickname: Jinspector
  receivers:
    - jinspector@foxmail.com
  title: Jinspector


schedule:
  report:
    # 24-hour time display
    initialTime: "16:57"
    # in seconds
    interval: 1*60*3
  inspect:
    initialTime: "10:18"
    interval: 120
```

An email report will be like this:
![Image text](https://github.com/javthon/Jinspector/blob/master/sample/result/report_desktop.png)



## Tips
Here are some inspectCommand tips:
```
netstat -ano        # look for ports
tasklist         # look processes on windows
ps aux | grep xxx.jar         # find process on linux
......
Waiting for contibuters' advice
```

## TODO
- web interface
- auto recover function
- use netty instead of tomcat for better performance
- complete the code comments and documents
- built-in inspect commands and auto find services
- set up docker version


## Contributing
Your contributions are always welcome!




