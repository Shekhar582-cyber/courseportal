// EduNest Chatbot — rule-based, no external API needed
(function () {

  // ── Knowledge base ────────────────────────────────────────────
  var KB = [
    // Greetings
    { p: /^(hi|hello|hey|good\s*(morning|evening|afternoon)|howdy)/i,
      r: ["Hi there! 👋 I'm Nesty, your EduNest assistant. How can I help you today?",
          "Hello! Welcome to EduNest 🪺 What can I help you with?"] },

    // What is EduNest
    { p: /what is edunest|about edunest|tell me about/i,
      r: ["EduNest is an online learning platform where you can enroll in courses, track your progress, take quizzes, and earn certificates. 🎓"] },

    // How to register
    { p: /register|sign up|create account/i,
      r: ["To register, click <a href='register.html'>Create Account</a> on the login page. You can sign up with email/password or use Google Sign-In. 🚀"] },

    // How to login
    { p: /login|sign in|log in/i,
      r: ["You can log in at <a href='login.html'>Login</a> using your email & password, or click 'Sign in with Google' for quick access. 🔑"] },

    // Forgot password
    { p: /forgot|reset password|password reset/i,
      r: ["No worries! Go to <a href='forgot-password.html'>Forgot Password</a>, enter your email, and you'll get a reset token. 🔐"] },

    // Courses
    { p: /find course|browse course|explore course|available course|what course/i,
      r: ["You can browse all courses at <a href='explore.html'>Explore Courses</a>. Filter by category, level, or price (free/paid). 📚"] },

    // Enroll
    { p: /how to enroll|enroll in|join course|start course/i,
      r: ["To enroll: go to <a href='explore.html'>Explore</a>, click on a course, then hit 'Enroll Now'. Free courses enroll instantly! ✅"] },

    // Dashboard
    { p: /dashboard|my course|enrolled course/i,
      r: ["Your <a href='dashboard.html'>Dashboard</a> shows all your enrolled courses, progress stats, notifications, and quiz scores. 📊"] },

    // Progress
    { p: /progress|track|completion|how far/i,
      r: ["Your progress is tracked automatically as you complete lessons. Check your dashboard for the progress bar on each course. 📈"] },

    // Quiz
    { p: /quiz|test|exam|question/i,
      r: ["Quizzes appear inside each lesson on the Course Content page. Complete the lesson, answer the questions, and submit to see your score instantly! 📝"] },

    // Certificate
    { p: /certificate|certif/i,
      r: ["You earn a certificate when you complete 100% of a course. Go to your dashboard, find the completed course, and click 'Certificate'. 🏆"] },

    // Leaderboard
    { p: /leaderboard|top student|ranking|rank/i,
      r: ["Check the <a href='leaderboard.html'>Leaderboard</a> to see top students by courses completed and daily streaks! 🥇"] },

    // Streak
    { p: /streak|daily|consecutive/i,
      r: ["Your learning streak increases every day you complete a lesson. Keep it going — streaks show on the leaderboard! 🔥"] },

    // Bookmark
    { p: /bookmark|save lesson/i,
      r: ["You can bookmark any lesson by clicking the 🔖 Bookmark button inside the course content page. View all bookmarks at <a href='bookmarks.html'>Bookmarks</a>."] },

    // Notes
    { p: /note|write note|my note/i,
      r: ["Click '📝 Add Note' on any lesson to write personal notes. They're saved to your account and available anytime. ✍️"] },

    // Discussion
    { p: /discussion|question|ask|q&a|forum/i,
      r: ["Each lesson has a 💬 Discussion section. Click it to ask questions or reply to others. Instructors can answer too! 🗣️"] },

    // Profile
    { p: /profile|my account|edit profile|update/i,
      r: ["Visit your <a href='profile.html'>Profile</a> page to update your name, bio, skills, photo, and change your password. 👤"] },

    // Theme
    { p: /dark mode|light mode|theme/i,
      r: ["Click the 'Theme' button in the navigation bar to toggle between dark and light mode. Your preference is saved automatically. 🌙"] },

    // Admin
    { p: /admin|manage course|manage user/i,
      r: ["Admins can manage courses at <a href='admin-courses.html'>Manage Courses</a>, view users at <a href='users.html'>Users</a>, and check analytics at <a href='admin-analytics.html'>Analytics</a>. 👑"] },

    // Instructor
    { p: /instructor|add lesson|upload|create quiz/i,
      r: ["Instructors can add lessons and quizzes via the <a href='instructor-panel.html'>Instructor Panel</a>. Select a course, fill in lesson details, and save! 📘"] },

    // Free courses
    { p: /free course|no cost|without paying/i,
      r: ["Yes! Many courses on EduNest are completely free. Filter by 'Free' on the <a href='explore.html'>Explore</a> page. 🎁"] },

    // Paid courses
    { p: /paid|premium|price|cost|fee/i,
      r: ["Premium courses require a one-time payment. You'll see the price on the course detail page. After payment, you're enrolled instantly. 💳"] },

    // Google sign in
    { p: /google|oauth|social login/i,
      r: ["EduNest supports Google Sign-In via Firebase. Just click 'Sign in with Google' on the login page — no password needed! 🔵"] },

    // Help / support
    { p: /help|support|contact|problem|issue/i,
      r: ["For help, you can reach us at admin@edunest.com or use the Discussion section in any lesson. We're here to help! 💬"] },

    // Thanks
    { p: /thank|thanks|thank you|thx/i,
      r: ["You're welcome! Happy learning on EduNest 🪺 Let me know if you need anything else!",
          "Anytime! Keep up the great work! 🌟"] },

    // Bye
    { p: /bye|goodbye|see you|exit/i,
      r: ["Goodbye! Keep learning and stay curious! 🚀", "See you soon! Happy studying! 📚"] },

    // Fallback
    { p: /.*/,
      r: ["I'm not sure about that. Try asking about courses, enrollment, quizzes, certificates, or your profile. 🤔",
          "Hmm, I didn't catch that. You can ask me things like 'How do I enroll?' or 'Where is my certificate?' 😊"] }
  ];

  function getReply(msg) {
    for (var i = 0; i < KB.length; i++) {
      if (KB[i].p.test(msg)) {
        var replies = KB[i].r;
        return replies[Math.floor(Math.random() * replies.length)];
      }
    }
    return "I'm not sure about that. Try asking about courses, enrollment, or quizzes!";
  }

  // ── Build UI ──────────────────────────────────────────────────
  var style = document.createElement('style');
  style.textContent = `
    #nesty-btn {
      position: fixed; bottom: 28px; right: 28px; z-index: 9999;
      width: 56px; height: 56px; border-radius: 50%;
      background: linear-gradient(135deg, #6366f1, #a855f7);
      border: none; cursor: pointer; font-size: 1.5rem;
      box-shadow: 0 8px 24px rgba(99,102,241,.5);
      display: flex; align-items: center; justify-content: center;
      transition: transform .2s;
    }
    #nesty-btn:hover { transform: scale(1.1); }
    #nesty-window {
      position: fixed; bottom: 96px; right: 28px; z-index: 9998;
      width: 340px; max-height: 480px;
      background: #12121f; border: 1px solid rgba(255,255,255,.12);
      border-radius: 20px; display: none; flex-direction: column;
      box-shadow: 0 24px 60px rgba(0,0,0,.5);
      overflow: hidden; font-family: 'Inter', sans-serif;
    }
    #nesty-window.open { display: flex; }
    #nesty-header {
      background: linear-gradient(135deg, #6366f1, #a855f7);
      padding: 14px 18px; display: flex; align-items: center;
      justify-content: space-between;
    }
    #nesty-header .title { color: #fff; font-weight: 700; font-size: .95rem; }
    #nesty-header .sub { color: rgba(255,255,255,.7); font-size: .75rem; }
    #nesty-close { background: none; border: none; color: #fff; font-size: 1.1rem; cursor: pointer; }
    #nesty-messages {
      flex: 1; overflow-y: auto; padding: 14px;
      display: flex; flex-direction: column; gap: 10px;
    }
    .nesty-msg { max-width: 85%; padding: 10px 14px; border-radius: 14px; font-size: .85rem; line-height: 1.5; }
    .nesty-msg.bot { background: rgba(255,255,255,.08); color: #e2e8f0; align-self: flex-start; border-bottom-left-radius: 4px; }
    .nesty-msg.user { background: linear-gradient(135deg,#6366f1,#a855f7); color: #fff; align-self: flex-end; border-bottom-right-radius: 4px; }
    .nesty-msg a { color: #c4b5fd; }
    #nesty-input-row { display: flex; gap: 8px; padding: 12px; border-top: 1px solid rgba(255,255,255,.08); }
    #nesty-input {
      flex: 1; padding: 10px 12px; border-radius: 10px;
      border: 1px solid rgba(255,255,255,.12); background: rgba(255,255,255,.07);
      color: #fff; font-family: 'Inter', sans-serif; font-size: .85rem; outline: none;
    }
    #nesty-input::placeholder { color: rgba(255,255,255,.35); }
    #nesty-send {
      padding: 10px 14px; border: none; border-radius: 10px;
      background: linear-gradient(135deg,#6366f1,#a855f7);
      color: #fff; font-weight: 700; cursor: pointer; font-size: .85rem;
    }
    .nesty-typing { display: flex; gap: 4px; align-items: center; padding: 10px 14px; }
    .nesty-typing span { width: 7px; height: 7px; border-radius: 50%; background: rgba(255,255,255,.4); animation: bounce .9s infinite; }
    .nesty-typing span:nth-child(2) { animation-delay: .15s; }
    .nesty-typing span:nth-child(3) { animation-delay: .3s; }
    @keyframes bounce { 0%,60%,100%{transform:translateY(0)} 30%{transform:translateY(-6px)} }
    .nesty-suggestions { display: flex; flex-wrap: wrap; gap: 6px; padding: 0 14px 10px; }
    .nesty-chip { padding: 5px 12px; border-radius: 999px; border: 1px solid rgba(99,102,241,.4);
      background: rgba(99,102,241,.1); color: #a78bfa; font-size: .75rem; cursor: pointer; }
    .nesty-chip:hover { background: rgba(99,102,241,.25); }
  `;
  document.head.appendChild(style);

  var btn = document.createElement('button');
  btn.id = 'nesty-btn';
  btn.innerHTML = '🪺';
  btn.title = 'Chat with Nesty';

  var win = document.createElement('div');
  win.id = 'nesty-window';
  win.innerHTML = `
    <div id="nesty-header">
      <div><div class="title">🪺 Nesty</div><div class="sub">EduNest Assistant • Online</div></div>
      <button id="nesty-close">✕</button>
    </div>
    <div id="nesty-messages"></div>
    <div class="nesty-suggestions" id="nesty-chips">
      <span class="nesty-chip" onclick="nestyAsk('How do I enroll?')">How to enroll?</span>
      <span class="nesty-chip" onclick="nestyAsk('Where is my certificate?')">Certificate</span>
      <span class="nesty-chip" onclick="nestyAsk('How do quizzes work?')">Quizzes</span>
      <span class="nesty-chip" onclick="nestyAsk('Show leaderboard')">Leaderboard</span>
    </div>
    <div id="nesty-input-row">
      <input id="nesty-input" placeholder="Ask me anything..." autocomplete="off">
      <button id="nesty-send">Send</button>
    </div>
  `;

  document.body.appendChild(btn);
  document.body.appendChild(win);

  var msgs = document.getElementById('nesty-messages');

  function addMsg(text, who) {
    var d = document.createElement('div');
    d.className = 'nesty-msg ' + who;
    d.innerHTML = text;
    msgs.appendChild(d);
    msgs.scrollTop = msgs.scrollHeight;
  }

  function showTyping() {
    var t = document.createElement('div');
    t.className = 'nesty-msg bot nesty-typing';
    t.id = 'nesty-typing';
    t.innerHTML = '<span></span><span></span><span></span>';
    msgs.appendChild(t);
    msgs.scrollTop = msgs.scrollHeight;
  }

  function hideTyping() {
    var t = document.getElementById('nesty-typing');
    if (t) t.remove();
  }

  function respond(userMsg) {
    addMsg(userMsg, 'user');
    showTyping();
    setTimeout(function () {
      hideTyping();
      addMsg(getReply(userMsg), 'bot');
    }, 600 + Math.random() * 400);
  }

  window.nestyAsk = function (q) { respond(q); };

  document.getElementById('nesty-send').addEventListener('click', function () {
    var inp = document.getElementById('nesty-input');
    var val = inp.value.trim();
    if (!val) return;
    inp.value = '';
    respond(val);
  });

  document.getElementById('nesty-input').addEventListener('keydown', function (e) {
    if (e.key === 'Enter') document.getElementById('nesty-send').click();
  });

  btn.addEventListener('click', function () {
    win.classList.toggle('open');
    if (win.classList.contains('open') && msgs.children.length === 0) {
      setTimeout(function () {
        addMsg("Hi! I'm Nesty 🪺 your EduNest assistant. Ask me about courses, enrollment, quizzes, certificates, or anything else!", 'bot');
      }, 300);
    }
  });

  document.getElementById('nesty-close').addEventListener('click', function () {
    win.classList.remove('open');
  });

})();
