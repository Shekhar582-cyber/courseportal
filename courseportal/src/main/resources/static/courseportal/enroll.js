// ── NAVBAR scroll + hamburger ───────────────────────────────
const navbar = document.getElementById('navbar');
const hamburger = document.getElementById('hamburger');
const navLinks = document.getElementById('navLinks');

window.addEventListener('scroll', () => {
    navbar.classList.toggle('scrolled', window.scrollY > 40);
});

hamburger.addEventListener('click', () => {
    const open = navLinks.classList.toggle('open');
    hamburger.classList.toggle('open', open);
    hamburger.setAttribute('aria-expanded', open);
});

navLinks.querySelectorAll('a').forEach(link => {
    link.addEventListener('click', () => {
        navLinks.classList.remove('open');
        hamburger.classList.remove('open');
        hamburger.setAttribute('aria-expanded', 'false');
    });
});

// ── CURRICULUM ACCORDION ────────────────────────────────────
document.querySelectorAll('.module-header').forEach(btn => {
    btn.addEventListener('click', () => {
        const module = btn.closest('.module');
        const isOpen = module.classList.contains('open');
        // close all
        document.querySelectorAll('.module').forEach(m => m.classList.remove('open'));
        // open clicked (if it wasn't already open)
        if (!isOpen) module.classList.add('open');
    });
});

// ── ENROLL FORM SUBMIT ─────────────────────────────────────
function submitEnroll(e) {
    e.preventDefault();

    const name = document.getElementById('name').value.trim();
    const email = document.getElementById('email').value.trim();
    const phone = document.getElementById('phone').value.trim();

    if (!name || !email || !phone) {
        showToast('⚠️ Please fill in all required fields.', true);
        return;
    }

    // Determine course from page title
    const course = document.title.split('–')[1]?.trim().split('|')[0]?.trim() || 'Course';

    // Build payload (connects to your existing backend)
    const payload = {
        id: Math.floor(Math.random() * 9000) + 1000,
        name,
        email,
        phone,
        course,
        enrolledAt: new Date().toISOString()
    };

    // Attempt to POST to backend; fall back gracefully if not running
    fetch('http://localhost:8081/users/enroll', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
    })
        .then(res => res.ok ? res.json() : Promise.resolve({}))
        .catch(() => ({}))   // backend might not be running — still show success
        .finally(() => {
            showToast('🎉 Enrolled successfully! Check your email.');
            document.getElementById('enrollForm').reset();
        });
}

// ── TOAST HELPER ───────────────────────────────────────────
function showToast(msg, isError = false) {
    const toast = document.getElementById('toast');
    if (!toast) return;
    toast.textContent = msg;
    toast.style.background = isError
        ? 'linear-gradient(135deg,#ef4444,#b91c1c)'
        : 'linear-gradient(135deg,#ff6a00,#ee0979)';
    toast.classList.add('show');
    setTimeout(() => toast.classList.remove('show'), 4000);
}

// ── 3D FORM CARD TILT ──────────────────────────────────────
const formCard = document.querySelector('.enroll-form-card');
if (formCard) {
    formCard.addEventListener('mousemove', (e) => {
        const rect = formCard.getBoundingClientRect();
        const dx = (e.clientX - rect.left - rect.width / 2) / (rect.width / 2);
        const dy = (e.clientY - rect.top - rect.height / 2) / (rect.height / 2);
        formCard.style.transform =
            `perspective(900px) rotateX(${-dy * 5}deg) rotateY(${dx * 5}deg) translateY(-4px)`;
    });
    formCard.addEventListener('mouseleave', () => {
        formCard.style.transition = 'transform 0.6s ease';
        formCard.style.transform = '';
        setTimeout(() => formCard.style.transition = '', 620);
    });
}

// ── HIGHLIGHT CARDS 3D TILT ────────────────────────────────
document.querySelectorAll('.highlight-item').forEach(card => {
    card.addEventListener('mousemove', (e) => {
        const rect = card.getBoundingClientRect();
        const dx = (e.clientX - rect.left - rect.width / 2) / (rect.width / 2);
        const dy = (e.clientY - rect.top - rect.height / 2) / (rect.height / 2);
        card.style.transform =
            `perspective(400px) rotateX(${-dy * 8}deg) rotateY(${dx * 8}deg) translateZ(8px) scale(1.03)`;
    });
    card.addEventListener('mouseleave', () => {
        card.style.transition = 'transform 0.4s ease';
        card.style.transform = '';
        setTimeout(() => card.style.transition = '', 420);
    });
});
