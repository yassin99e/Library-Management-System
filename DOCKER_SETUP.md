# 🐳 Library Management System - Docker Setup

Ce guide vous explique comment démarrer l'ensemble du système avec Docker.

## 📋 Prérequis

- Docker Desktop installé (version 20.10+)
- Docker Compose (version 2.0+)
- Au moins 8 GB de RAM disponible pour Docker
- Ports suivants disponibles : 8080, 8081, 8082, 8084, 8761, 9999, 4200, 3308, 9092, 2181

## 🏗️ Architecture

Le système comprend les composants suivants :

### Infrastructure
- **MySQL** (port 3308) : Base de données principale
- **Kafka** (port 9092) : Message broker
- **Zookeeper** (port 2181) : Coordination pour Kafka

### Services Spring Cloud
- **Discovery Service** (port 8761) : Eureka Server pour la découverte de services
- **Config Service** (port 9999) : Configuration centralisée

### Microservices Métier
- **Book Service** (port 8082) : Gestion des livres
- **Borrower Service** (port 8081) : Gestion des emprunteurs et authentification
- **Notification Service** (port 8084) : Gestion des notifications

### API & Frontend
- **API Gateway** (port 8080) : Point d'entrée unique pour les APIs
- **Frontend Angular** (port 4200) : Interface utilisateur

## 🚀 Démarrage rapide

### 1. Cloner le projet et naviguer vers le répertoire
```bash
cd "Library Management System"
```

### 2. Démarrer tous les services
```bash
docker-compose up -d
```

Cette commande va :
1. Construire les images Docker pour tous les microservices
2. Démarrer les services dans le bon ordre grâce aux health checks
3. Créer les bases de données MySQL automatiquement

### 3. Vérifier le statut des conteneurs
```bash
docker-compose ps
```

Tous les services doivent avoir le statut "healthy" (cela peut prendre 2-3 minutes).

### 4. Suivre les logs
```bash
# Tous les services
docker-compose logs -f

# Un service spécifique
docker-compose logs -f book-service
docker-compose logs -f api-gateway
docker-compose logs -f frontend
```

## 🔍 Vérification du déploiement

### Eureka Dashboard
Ouvrez http://localhost:8761 pour voir tous les services enregistrés.

### API Gateway
Testez l'API Gateway : http://localhost:8080/actuator/health

### Frontend
Accédez à l'application : http://localhost:4200

### Swagger UI (si disponible)
- Book Service : http://localhost:8082/swagger-ui.html
- Borrower Service : http://localhost:8081/swagger-ui.html

## 🛑 Arrêter les services

### Arrêt simple (garde les volumes)
```bash
docker-compose down
```

### Arrêt complet (supprime les volumes/données)
```bash
docker-compose down -v
```

## 🔄 Redémarrage après modifications

### Rebuild un service spécifique
```bash
docker-compose build book-service
docker-compose up -d book-service
```

### Rebuild tous les services
```bash
docker-compose build
docker-compose up -d
```

### Rebuild sans cache
```bash
docker-compose build --no-cache
docker-compose up -d
```

## 🐛 Dépannage

### Les services ne démarrent pas dans l'ordre
Les health checks garantissent l'ordre de démarrage. Attendez 2-3 minutes pour que tous les services soient "healthy".

### Problème de connexion à MySQL
Vérifiez que le conteneur MySQL est healthy :
```bash
docker-compose ps mysql
docker-compose logs mysql
```

### Problème de connexion à Kafka
Vérifiez Kafka et Zookeeper :
```bash
docker-compose logs kafka
docker-compose logs zookeeper
```

### Service ne s'enregistre pas dans Eureka
Vérifiez que Discovery Service est démarré :
```bash
docker-compose logs discovery-service
```

### Rebuild complet en cas de problème
```bash
docker-compose down -v
docker system prune -a
docker-compose build --no-cache
docker-compose up -d
```

## 📊 Monitoring

### Vérifier l'utilisation des ressources
```bash
docker stats
```

### Accéder à un conteneur
```bash
docker exec -it library-book-service sh
docker exec -it library-mysql bash
```

### Inspecter les logs d'un conteneur
```bash
docker logs library-book-service
docker logs library-mysql
```

## 🔧 Configuration

### Variables d'environnement
Les variables d'environnement sont définies dans `docker-compose.yml`. Pour les modifier :
1. Éditez le fichier `docker-compose.yml`
2. Relancez les services concernés

### Bases de données
Les bases de données sont automatiquement créées via le script `init-db.sql` :
- `Book_db` : Pour le Book Service
- `User_db` : Pour le Borrower Service
- `notification_db` : Pour le Notification Service

### Ports personnalisés
Pour changer un port, modifiez la section `ports` dans `docker-compose.yml` :
```yaml
ports:
  - "NOUVEAU_PORT:PORT_INTERNE"
```

## 📈 Ordre de démarrage des services

Grâce aux health checks, les services démarrent automatiquement dans cet ordre :

1. **Infrastructure** : MySQL, Zookeeper, Kafka
2. **Discovery Service** (Eureka)
3. **Config Service**
4. **Microservices** : Book, Borrower, Notification (en parallèle)
5. **API Gateway**
6. **Frontend**

## 💾 Volumes Docker

Les données persistantes sont stockées dans des volumes Docker :
- `mysql-data` : Données MySQL
- `kafka-data` : Données Kafka
- `zookeeper-data` : Données Zookeeper
- `zookeeper-logs` : Logs Zookeeper

Pour nettoyer les volumes :
```bash
docker volume ls
docker volume rm <volume_name>
```

## 🌐 Réseau Docker

Tous les services communiquent via le réseau `library-network`.

Pour inspecter le réseau :
```bash
docker network inspect library-network
```

## 🎯 URLs importantes

| Service | URL | Description |
|---------|-----|-------------|
| Frontend | http://localhost:4200 | Application Angular |
| API Gateway | http://localhost:8080 | Point d'entrée API |
| Eureka | http://localhost:8761 | Dashboard Eureka |
| Config Server | http://localhost:9999 | Configuration centralisée |
| Book Service | http://localhost:8082 | API des livres |
| Borrower Service | http://localhost:8081 | API des emprunteurs |
| Notification Service | http://localhost:8084 | API des notifications |
| MySQL | localhost:3308 | Base de données |
| Kafka | localhost:9092 | Message broker |

## 📝 Notes importantes

1. **Premier démarrage** : Le premier build peut prendre 5-10 minutes car Maven doit télécharger toutes les dépendances
2. **Health checks** : Les services attendent que leurs dépendances soient "healthy" avant de démarrer
3. **Ressources** : Assurez-vous d'avoir au moins 8 GB de RAM alloués à Docker
4. **Logs** : Utilisez `docker-compose logs -f` pour suivre les logs en temps réel

## ✅ Checklist de vérification

- [ ] Docker Desktop est démarré
- [ ] Ports requis sont disponibles
- [ ] `docker-compose up -d` exécuté sans erreur
- [ ] Tous les conteneurs sont "healthy" (docker-compose ps)
- [ ] Eureka Dashboard accessible (http://localhost:8761)
- [ ] Tous les services sont enregistrés dans Eureka
- [ ] Frontend accessible (http://localhost:4200)
- [ ] API Gateway répond (http://localhost:8080/actuator/health)

## 🆘 Support

En cas de problème :
1. Vérifiez les logs : `docker-compose logs -f`
2. Vérifiez les health checks : `docker-compose ps`
3. Redémarrez les services : `docker-compose restart`
4. Rebuild complet : `docker-compose down -v && docker-compose build --no-cache && docker-compose up -d`
