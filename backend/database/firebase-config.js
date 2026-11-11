/**
 * Firebase Firestore & Authentication Configuration for AI Video Studio Pro
 */

let admin;
try {
  admin = require('firebase-admin');
} catch (e) {
  admin = null;
}

let db = null;
let auth = null;

if (admin && process.env.FIREBASE_SERVICE_ACCOUNT_KEY) {
  try {
    const serviceAccount = JSON.parse(process.env.FIREBASE_SERVICE_ACCOUNT_KEY);
    admin.initializeApp({
      credential: admin.credential.cert(serviceAccount)
    });
    db = admin.firestore();
    auth = admin.auth();
    console.log('[FIREBASE] Connected to Firestore & Firebase Auth successfully');
  } catch (err) {
    console.warn('[FIREBASE INIT WARNING] Could not initialize Firebase Admin:', err.message);
  }
}

module.exports = {
  admin,
  db,
  auth
};
