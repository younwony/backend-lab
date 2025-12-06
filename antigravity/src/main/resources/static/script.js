document.addEventListener('DOMContentLoaded', () => {
    console.log('Welcome to the new simple web page!');

    // Add simple hover effect logic if needed beyond CSS
    const cards = document.querySelectorAll('.card');

    cards.forEach(card => {
        card.addEventListener('mouseenter', () => {
            card.style.borderColor = 'rgba(99, 102, 241, 0.3)';
        });

        card.addEventListener('mouseleave', () => {
            card.style.borderColor = 'rgba(255, 255, 255, 0.08)';
        });
    });
});

/* =========================================
   Action Page Logic
   ========================================= */

// Clock functionality
function updateClock() {
    const clockElement = document.getElementById('clock');
    if (clockElement) {
        const now = new Date();
        clockElement.innerText = now.toLocaleTimeString('en-GB', { timeZone: 'Asia/Seoul', hour12: false });
    }
}

setInterval(updateClock, 1000);
updateClock();

// Terminal functionality
const terminal = document.getElementById('terminal');

function logToTerminal(message, type = 'info') {
    if (!terminal) return;

    const line = document.createElement('div');
    line.className = 'line';

    const timestamp = new Date().toLocaleTimeString('en-GB', { timeZone: 'Asia/Seoul', hour12: false });
    let prefix = `<span class="prompt">[${timestamp}] ></span>`;

    if (type === 'error') {
        line.innerHTML = `${prefix} <span class="error">${message}</span>`;
    } else if (type === 'success') {
        line.innerHTML = `${prefix} <span class="success">${message}</span>`;
    } else if (type === 'warning') {
        line.innerHTML = `${prefix} <span class="warning">${message}</span>`;
    } else {
        line.innerHTML = `${prefix} ${message}`;
    }

    terminal.appendChild(line);
    terminal.scrollTop = terminal.scrollHeight;
}

// Simulation Logic
function runSequence(action) {
    if (!terminal) return;

    switch (action) {
        case 'init':
            logToTerminal('Initializing system sequence...', 'info');
            setTimeout(() => logToTerminal('Checking core integrity...', 'info'), 500);
            setTimeout(() => logToTerminal('Core integrity: 100%', 'success'), 1200);
            setTimeout(() => logToTerminal('Sequence initialized.', 'success'), 1500);
            break;
        case 'scan':
            logToTerminal('Starting deep scan...', 'info');
            let progress = 0;
            const interval = setInterval(() => {
                progress += 10;
                logToTerminal(`Scanning... ${progress}%`, 'info');
                if (progress >= 100) {
                    clearInterval(interval);
                    logToTerminal('Deep scan complete. No anomalies found.', 'success');
                }
            }, 300);
            break;
        case 'purge':
            logToTerminal('Initiating cache purge...', 'warning');
            setTimeout(() => logToTerminal('Clearing temporary buffers...', 'info'), 600);
            setTimeout(() => logToTerminal('Cache purged successfully.', 'success'), 1200);
            break;
        case 'emergency':
            logToTerminal('EMERGENCY STOP TRIGGERED!', 'error');
            logToTerminal('Halting all processes...', 'error');
            logToTerminal('System halted.', 'error');
            break;
        default:
            logToTerminal('Unknown command.', 'error');
    }
}

// Random Metric Simulation
function simulateMetrics() {
    const tempVal = document.getElementById('temp-val');
    const tempBar = document.getElementById('temp-bar');
    const memVal = document.getElementById('mem-val');
    const memBar = document.getElementById('mem-bar');
    const netVal = document.getElementById('net-val');

    if (tempVal && tempBar) {
        const temp = Math.floor(Math.random() * 20) + 30; // 30-50
        tempVal.innerText = temp;
        tempBar.style.width = `${temp}%`;
    }

    if (memVal && memBar) {
        const mem = Math.floor(Math.random() * 30) + 10; // 10-40
        memVal.innerText = mem;
        memBar.style.width = `${mem}%`;
    }

    if (netVal) {
        const net = Math.floor(Math.random() * 500) + 500; // 500-1000
        netVal.innerText = net;
    }
}

setInterval(simulateMetrics, 2000);
