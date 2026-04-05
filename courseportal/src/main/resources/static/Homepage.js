// ── NAVBAR scroll effect ────────────────────────────────────
const navbar = document.getElementById('navbar');
window.addEventListener('scroll', () => {
    navbar.classList.toggle('scrolled', window.scrollY > 40);
});

// ── HAMBURGER menu ──────────────────────────────────────────
const hamburger = document.getElementById('hamburger');
const navLinks = document.getElementById('navLinks');

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

// ── SCROLL REVEAL ───────────────────────────────────────────
const revealEls = document.querySelectorAll('.reveal');
const observer = new IntersectionObserver(
    (entries) => entries.forEach(e => {
        if (e.isIntersecting) { e.target.classList.add('visible'); }
    }),
    { threshold: 0.12 }
);
revealEls.forEach(el => observer.observe(el));

// ── COUNTER animation ──────────────────────────────────────
function animateCounters() {
    document.querySelectorAll('.stat-num').forEach(el => {
        const target = +el.dataset.target;
        const duration = 1600;
        const step = target / (duration / 16);
        let current = 0;
        const timer = setInterval(() => {
            current += step;
            if (current >= target) { current = target; clearInterval(timer); }
            el.textContent = Math.floor(current).toLocaleString();
        }, 16);
    });
}

const statsSection = document.querySelector('.stats');
const statsObserver = new IntersectionObserver(
    (entries) => {
        if (entries[0].isIntersecting) { animateCounters(); statsObserver.disconnect(); }
    },
    { threshold: 0.3 }
);
if (statsSection) statsObserver.observe(statsSection);

// ══════════════════════════════════════════════════
//  3D CARD TILT  (mouse-tracking perspective tilt)
// ══════════════════════════════════════════════════
const MAX_TILT = 16;   // degrees
const LIFT_PX = 20;   // translateZ lift on hover

document.querySelectorAll('[data-tilt]').forEach(card => {
    let raf = null;

    card.addEventListener('mousemove', (e) => {
        if (raf) cancelAnimationFrame(raf);
        raf = requestAnimationFrame(() => {
            const rect = card.getBoundingClientRect();
            const cx = rect.left + rect.width / 2;
            const cy = rect.top + rect.height / 2;
            const dx = (e.clientX - cx) / (rect.width / 2);  // -1 … 1
            const dy = (e.clientY - cy) / (rect.height / 2);  // -1 … 1
            const rotY = dx * MAX_TILT;
            const rotX = -dy * MAX_TILT;

            card.style.transform =
                `perspective(900px) rotateX(${rotX}deg) rotateY(${rotY}deg) translateZ(${LIFT_PX}px) scale(1.03)`;
        });
    });

    card.addEventListener('mouseleave', () => {
        if (raf) cancelAnimationFrame(raf);
        card.style.transition = 'transform 0.55s cubic-bezier(0.22, 1, 0.36, 1)';
        card.style.transform = 'perspective(900px) rotateX(0deg) rotateY(0deg) translateZ(0px) scale(1)';
        // Remove the explicit transition after it finishes so mousemove is snappy again
        card.addEventListener('transitionend', () => {
            card.style.transition = '';
        }, { once: true });
    });
});

// ══════════════════════════════════════════════════
//  HERO PARALLAX MOUSE EFFECT
//  Moves the orbs and hero content in response to
//  mouse position for a subtle depth illusion.
// ══════════════════════════════════════════════════
const heroSection = document.getElementById('hero-section');
const heroContent = document.querySelector('.hero-content');
const orbs = document.querySelectorAll('.hero-orb');

if (heroSection && heroContent) {
    let heroRaf = null;

    heroSection.addEventListener('mousemove', (e) => {
        if (heroRaf) cancelAnimationFrame(heroRaf);
        heroRaf = requestAnimationFrame(() => {
            const rect = heroSection.getBoundingClientRect();
            const mx = (e.clientX - rect.left) / rect.width - 0.5; // -0.5 … 0.5
            const my = (e.clientY - rect.top) / rect.height - 0.5;

            // Content shifts slightly opposite to mouse (parallax depth)
            heroContent.style.transform =
                `perspective(1000px) rotateX(${-my * 5}deg) rotateY(${mx * 5}deg)`;

            // Orbs drift in the direction of mouse movement (deeper layer)
            orbs.forEach((orb, i) => {
                const factor = (i + 1) * 18;
                orb.style.transform =
                    `translate(${mx * factor}px, ${my * factor}px)`;
            });
        });
    });

    heroSection.addEventListener('mouseleave', () => {
        if (heroRaf) cancelAnimationFrame(heroRaf);
        heroContent.style.transition = 'transform 0.8s ease';
        heroContent.style.transform = 'perspective(1000px) rotateX(0deg) rotateY(0deg)';
        orbs.forEach(orb => {
            orb.style.transition = 'transform 0.8s ease';
            orb.style.transform = 'translate(0,0)';
        });
        setTimeout(() => {
            heroContent.style.transition = '';
            orbs.forEach(o => o.style.transition = '');
        }, 820);
    });
}

// ══════════════════════════════════════════════════
//  VISUAL CARDS TILT (About section)
// ══════════════════════════════════════════════════
document.querySelectorAll('.visual-card').forEach(card => {
    card.addEventListener('mousemove', (e) => {
        const rect = card.getBoundingClientRect();
        const dx = (e.clientX - rect.left - rect.width / 2) / (rect.width / 2);
        const dy = (e.clientY - rect.top - rect.height / 2) / (rect.height / 2);
        card.style.transform =
            `perspective(600px) rotateX(${-dy * 10}deg) rotateY(${dx * 10}deg) scale(1.06) translateZ(16px)`;
    });

    card.addEventListener('mouseleave', () => {
        card.style.transition = 'transform 0.5s cubic-bezier(0.22,1,0.36,1)';
        card.style.transform = '';
        setTimeout(() => card.style.transition = '', 520);
    });
});
