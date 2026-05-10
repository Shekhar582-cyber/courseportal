(function(){
  // Load theme: prefer server-saved preference, fallback to localStorage
  function applyTheme(theme) {
    if (theme === 'light') {
      document.body.classList.add('light');
    } else {
      document.body.classList.remove('light');
    }
    localStorage.setItem('cp-theme', theme);
  }

  // Apply saved theme immediately to avoid flash
  var saved = localStorage.getItem('cp-theme') || 'dark';
  document.addEventListener('DOMContentLoaded', function(){
    applyTheme(saved);
    // If user is logged in, sync theme from their profile
    var email = sessionStorage.getItem('userEmail') || localStorage.getItem('userEmail');
    if (email) {
      var userId = sessionStorage.getItem('userId') || localStorage.getItem('userId');
      if (userId) {
        fetch('/users/' + userId).then(function(r){ return r.json(); }).then(function(u){
          if (u && u.theme) {
            applyTheme(u.theme);
          }
        }).catch(function(){});
      }
    }
  });

  window.toggleTheme = function(){
    var isLight = document.body.classList.toggle('light');
    var theme = isLight ? 'light' : 'dark';
    localStorage.setItem('cp-theme', theme);
    // Persist to backend if logged in
    var email = sessionStorage.getItem('userEmail') || localStorage.getItem('userEmail');
    if (email) {
      fetch('/users/theme', {
        method: 'PUT',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({email: email, theme: theme})
      }).catch(function(){});
    }
  };
})();
