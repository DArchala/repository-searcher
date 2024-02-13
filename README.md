# Repository searcher

 What is the application destiny?  
 Find github repositories by username, get its branches information and return it to the user.

## Technologies
1. Java 21
2. Spring boot 3.2.2
3. Hibernate
4. JUnit 5
5. Maven

## How to run using Docker?

1. Build docker image:  
docker build -t repository-searcher:latest .

2. Run docker container:  
docker run --name repository-searcher -d -p 8080:8080 repository-searcher:latest
 
3. If you want to close the application, stop and delete container:  
docker rm -f repository-searcher

## How to run using Intellij IDEA?

1. Clone repository to your pc:  
git clone https://github.com/DArchala/repository-searcher.git

2. Download and install jdk-21
 
3. Download and install apache-maven, version 3.8.5 or newer

4. Find java class in project:  
src/main/java/pl/archala/repositorysearcher/RepositorySearcherApplication.java

5. Run main method
