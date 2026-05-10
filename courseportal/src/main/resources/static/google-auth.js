// google-auth.js
// Handles Google Sign-In using Firebase Auth and forwards ID token to backend.

function storeAuthSession(data) {
  var role = data.role || 'STUDENT';
  var name = data.name || '';
  var email = data.email || '';
  var id = String(data.id || '');
  sessionStorage.setItem('userRole', role);
  sessionStorage.setItem('userName', name);
  sessionStorage.setItem('userEmail', email);
  sessionStorage.setItem('userId', id);
  localStorage.setItem('userRole', role);
  localStorage.setItem('userName', name);
  localStorage.setItem('userEmail', email);
  localStorage.setItem('userId', id);
}

function googleProvider() {
  var provider = new firebase.auth.GoogleAuthProvider();
  provider.addScope('email');
  provider.addScope('profile');
  provider.setCustomParameters({ prompt: 'select_account' });
  return provider;
}

function sendGoogleUserToBackend(googleUser, googleProfile) {
  return googleUser.getIdToken().then(function(idToken) {
    return fetch('/users/google-login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        idToken: idToken,
        name: (googleUser && googleUser.displayName) || (googleProfile && googleProfile.name) || '',
        email: (googleUser && googleUser.email) || (googleProfile && googleProfile.email) || (googleUser && googleUser.providerData && googleUser.providerData[0] && googleUser.providerData[0].email) || ''
      })
    });
  })
  .then(function(response) {
    if (!response.ok) {
      return response.json().catch(function() { return {}; }).then(function(data) {
        throw new Error(data.message || 'Backend responded with error');
      });
    }
    return response.json();
  })
  .then(function(data) {
    storeAuthSession(data);
    return data;
  });
}

function completeGoogleAuth(result, successMessage, defaultRedirect) {
  var googleUser = result && result.user ? result.user : null;
  var googleProfile = result && result.additionalUserInfo && result.additionalUserInfo.profile ? result.additionalUserInfo.profile : null;
  if (!googleUser) {
    return Promise.resolve(null);
  }
  return sendGoogleUserToBackend(googleUser, googleProfile).then(function(data) {
    if (typeof showToast === 'function') {
      showToast(successMessage, 'success');
    }
    setTimeout(function() {
      window.location.href = data.redirectUrl || defaultRedirect || 'dashboard.html';
    }, 800);
    return data;
  }).catch(function(err) {
    console.error('completeGoogleAuth error:', err);
    var msg = err && err.message ? err.message : 'Google sign-in failed';
    if (typeof showToast === 'function') {
      showToast(msg, 'error');
    } else {
      alert('Google sign-in error: ' + msg);
    }
  });
}

function googleSignIn() {
  auth.signInWithPopup(googleProvider())
    .then(function(result) {
      return completeGoogleAuth(result, 'Google Sign-In Successful!', 'dashboard.html');
    })
    .catch(function(error) {
      console.error('Google sign-in error:', error);
      var msg = error && error.message ? error.message : 'Google sign-in failed';
      if (typeof showToast === 'function') {
        showToast(msg, 'error');
      } else {
        alert(msg);
      }
    });
}

function googleSignUp() {
  auth.signInWithPopup(googleProvider())
    .then(function(result) {
      return completeGoogleAuth(result, 'Google Sign-Up Successful!', 'dashboard.html');
    })
    .catch(function(error) {
      console.error('Google sign-up error:', error);
      var msg = error && error.message ? error.message : 'Google sign-up failed';
      if (typeof showToast === 'function') {
        showToast(msg, 'error');
      } else {
        alert(msg);
      }
    });
}


