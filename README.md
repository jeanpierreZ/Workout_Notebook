# Workout_Notebook

Enregistrez et partagez vos séances d'entraînement

OpenClassrooms Développeur d'application - Android 11ème projet

Workout Notebook est une application mobile sous Android qui permet d'effectuer, enregistrer, consulter et partager ses séances d'entraînement.

### Les fonctionnalités :
- Se connecter avec une adresse mail, par Google ou Facebook.
- Créer son profil.
- Créer des exercices, incluant un nombre de séries, de répétitions avec ou sans unité (km, kg...) ainsi que des workouts types composés des exercices choisis.
- Ajouter ou modifier une session d'entraînement sur le calendrier.
- Consulter l'historique d'un entraînement sur le calendrier.
- Effectuer et enregistrer sa séance d'entraînement directement depuis son smartphone. Affichage de la série de l'exercise en cours et de celle à venir, ainsi que d'un compte à rebours pour les temps de repos.
- Consulter l'historique d'un exercise, sur une période de temps choisie, sous forme de graphique.
- Rechercher et suivre d'autres utilisateurs, voir ses followers et consulter leurs 5 dernières séances d'entraînement.
- Recevoir une notification une heure avant le début de la séance.

### Tech

- Language : Kotlin for the mobile app and javascript for the cloud functions.
- MVVM architectural pattern.
- Firebase for the back-end, with Auth for the authentication, Firestore for the data, Storage for images and Cloud Functions to communicate with Algolia.
- Algolia for the search implementation.
- Facebook login support also for Auth.

### Screenshots

<img src="https://github.com/jeanpierreZ/Workout_Notebook/blob/screenshot/app/src/main/res/drawable/screenshot_home.png?raw=true" width="30%" height="30%"/>
<img src="https://github.com/jeanpierreZ/Workout_Notebook/blob/screenshot/app/src/main/res/drawable/screenshot_profile.png?raw=true" width="30%" height="30%"/>
<img src="https://github.com/jeanpierreZ/Workout_Notebook/blob/screenshot/app/src/main/res/drawable/screenshot_calendar.png?raw=true" width="30%" height="30%"/>
<img src="https://github.com/jeanpierreZ/Workout_Notebook/blob/screenshot/app/src/main/res/drawable/screenshot_search.png?raw=true" width="30%" height="30%"/>
<img src="https://github.com/jeanpierreZ/Workout_Notebook/blob/screenshot/app/src/main/res/drawable/screenshot_statistics.png?raw=true" width="30%" height="30%"/>
<img src="https://github.com/jeanpierreZ/Workout_Notebook/blob/screenshot/app/src/main/res/drawable/screenshot_training_session.png?raw=true" width="30%" height="30%"/>

Image by David Mark from Pixabay.
Icon launcher made by Nikita Golubev from www.flaticon.com.

Jean-Pierre Zingraff