// firebase-config.js
// Firebase initialization for Course Portal
// NOTE: Replace the placeholder values with your actual Firebase project credentials if they change.

// The Firebase SDK has already been loaded via <script> tags in login.html.
// Initialize the app with the configuration object.

var firebaseConfig = {
  apiKey: "AIzaSyAG-TXU0vuEOfThWZYRjH2SyL-yTc_b1Ds",
  authDomain: "gecn-9bcab.firebaseapp.com",
  projectId: "gecn-9bcab",
  storageBucket: "gecn-9bcab.firebasestorage.app",
  messagingSenderId: "299913246697",
  appId: "1:299913246697:web:4e1bf784c013afbf1a7d78"
};

// Initialize Firebase
firebase.initializeApp(firebaseConfig);

// Expose auth for other scripts
var auth = firebase.auth();
