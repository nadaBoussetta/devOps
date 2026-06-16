// ── Horloge ──────────────────────────────────────────────
function updateClock() {
    const now  = new Date();
    const time = now.toLocaleTimeString('fr-FR');
    const date = now.toLocaleDateString('fr-FR', { weekday: 'long', day: 'numeric', month: 'long' });
    document.getElementById('clock-time').textContent = time;
    document.getElementById('clock-date').textContent = date.charAt(0).toUpperCase() + date.slice(1);
}
updateClock();
setInterval(updateClock, 1000);

// ── Citations ─────────────────────────────────────────────
const quotes = [
    { text: "Une maison sans livres est comme une pièce sans fenêtres.", author: "Heinrich Mann" },
    { text: "La lecture est à l'esprit ce que l'exercice est au corps.", author: "Joseph Addison" },
    { text: "Les livres sont des miroirs : on n'y voit que ce qu'on a déjà en soi.", author: "Carlos Ruiz Zafón" },
    { text: "Il n'existe pas d'ami aussi loyal qu'un livre.", author: "Ernest Hemingway" },
    { text: "Lire, c'est boire et manger. L'esprit qui ne lit pas maigrit comme le corps qui ne mange pas.", author: "Victor Hugo" },
    { text: "Une bibliothèque est un endroit où les morts gardent leurs secrets.", author: "Josh Billings" },
    { text: "Tout ce que je suis, je le dois aux livres.", author: "Abraham Lincoln" },
];

let currentQuote = 0;

function showQuote(index, animate = true) {
    const card   = document.getElementById('quote-card');
    const text   = document.getElementById('quote-text');
    const author = document.getElementById('quote-author');
    const dots   = document.getElementById('quote-dots');

    if (animate) { card.style.opacity = '0'; card.style.transform = 'translateY(8px)'; }

    setTimeout(() => {
        text.textContent   = quotes[index].text;
        author.textContent = '— ' + quotes[index].author;
        dots.innerHTML = quotes.map((_, i) =>
            `<span class="qdot ${i === index ? 'active' : ''}" data-i="${i}"></span>`
        ).join('');
        dots.querySelectorAll('.qdot').forEach(d => {
            d.addEventListener('click', () => { currentQuote = parseInt(d.dataset.i); showQuote(currentQuote); });
        });
        card.style.opacity = '1'; card.style.transform = 'translateY(0)';
    }, animate ? 300 : 0);
}
showQuote(0, false);
setInterval(() => { currentQuote = (currentQuote + 1) % quotes.length; showQuote(currentQuote); }, 5000);

// ── Compteurs animés ──────────────────────────────────────
function animateCounter(el) {
    const target = parseInt(el.dataset.target);
    const steps  = 1800 / 16;
    let current  = 0;
    const timer  = setInterval(() => {
        current += target / steps;
        if (current >= target) { current = target; clearInterval(timer); }
        el.textContent = Math.floor(current).toLocaleString('fr-FR');
    }, 16);
}
const observer = new IntersectionObserver(entries => {
    entries.forEach(e => { if (e.isIntersecting) { animateCounter(e.target); observer.unobserve(e.target); } });
}, { threshold: 0.5 });
document.querySelectorAll('.stat-number').forEach(el => observer.observe(el));

// ── Compteur bibliothèques ouvertes ───────────────────────
function updateOpenCount() {
    const h = new Date().getHours();
    let base = h >= 9 && h < 12 ? 180 : h >= 12 && h < 14 ? 120 : h >= 14 && h < 18 ? 160 : h >= 18 && h < 20 ? 60 : 5;
    base += Math.floor(Math.random() * 20);
    document.getElementById('open-count').textContent = base;
}
updateOpenCount();
setInterval(updateOpenCount, 30000);

// ── Particules ────────────────────────────────────────────
const canvas = document.getElementById('particles-canvas');
const ctx    = canvas.getContext('2d');
function resize() { canvas.width = window.innerWidth; canvas.height = window.innerHeight; }
resize(); window.addEventListener('resize', resize);
const particles = Array.from({ length: 38 }, () => ({
    x: Math.random() * window.innerWidth, y: Math.random() * window.innerHeight,
    r: Math.random() * 1.4 + 0.3, vx: (Math.random() - 0.5) * 0.3, vy: (Math.random() - 0.5) * 0.3,
    a: Math.random() * 0.4 + 0.1,
}));
function drawParticles() {
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    particles.forEach(p => {
        ctx.beginPath(); ctx.arc(p.x, p.y, p.r, 0, Math.PI * 2);
        ctx.fillStyle = `rgba(2,71,100,${p.a})`; ctx.fill();
        p.x += p.vx; p.y += p.vy;
        if (p.x < 0 || p.x > canvas.width)  p.vx *= -1;
        if (p.y < 0 || p.y > canvas.height) p.vy *= -1;
    });
    requestAnimationFrame(drawParticles);
}
drawParticles();