const functions = require('firebase-functions');

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//   functions.logger.info("Hello logs!", {structuredData: true});
//   response.send("Hello from Firebase!");
// });

// The Firebase Admin SDK to access Cloud Firestore.
const admin = require('firebase-admin');
admin.initializeApp();

// Initialize Algolia, requires installing Algolia dependencies:
// https://www.algolia.com/doc/api-client/javascript/getting-started/#install
//
// App ID and API Key are stored in functions config variables
const ALGOLIA_ID = functions.config().algolia.app_id;
const ALGOLIA_ADMIN_KEY = functions.config().algolia.api_key;
const ALGOLIA_SEARCH_KEY = functions.config().algolia.search_key;

const ALGOLIA_INDEX_NAME = 'users';

const algoliasearch = require("algoliasearch");

const client = algoliasearch(ALGOLIA_ID, ALGOLIA_ADMIN_KEY);

const index = client.initIndex(ALGOLIA_INDEX_NAME);

// Cloud Functions

// Update the search index every time a user is written for the first time.
exports.onUserCreated = functions.firestore
.document('users/{userId}')
.onCreate((snap, context) => {
	// Get the user document
	const user = snap.data();

	// Add an 'objectID' field which Algolia requires
	user.objectID = context.params.userId;

	// Write to the algolia index
	return index.saveObject(user);
});

// Update the search index every time a user is updated.
exports.onUserUpdated = functions.firestore
.document('users/{userId}')
.onUpdate((change, context) => {
	// Get the user document
	const user = change.after.data();
	
	// Add an 'objectID' field which Algolia requires
	user.objectID = context.params.userId;
	
	// Write to the algolia index
	return index.saveObject(user);
}); 

// Update the search index every time a user is deleted.
exports.onUserDeleted = functions.firestore
.document('users/{userId}')
.onDelete(snapshot => 
// Delete to the Algolia index
index.deleteObject(snapshot.id)
); 