# Étape 1 : Build de l'application
FROM maven:3.8.5-openjdk-17 AS build
COPY src /home/app/src
COPY pom.xml /home/app
# le fichier .jar créé par Maven est placé dans un dossier virtuel nommé /home/app/target/.
RUN mvn -f /home/app/pom.xml clean package

# Etape 2: Exécution
#Une fois le .jar créé, on change d'environnement pour créer l'image finale la plus légère possible
FROM openjdk:17-jdk-slim
COPY --from=build /home/app/target/backend-0.0.1-SNAPSHOT.jar /usr/local/lib/backend.jar
EXPOSE 8088
# on exécute la commande: java -jar /usr/local/lib/backend.jar pour lancer l'application".via la commande: docker build -t mon-application
ENTRYPOINT ["java","-jar","/usr/local/lib/backend.jar"]
