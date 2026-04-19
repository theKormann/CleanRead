document.getElementById('clean-btn').addEventListener('click', async () => {
    const btn = document.getElementById('clean-btn');
    const btnText = document.getElementById('btn-text');
    const spinner = document.getElementById('spinner');
    const errorMsg = document.getElementById('error-msg');
    
    // Estado de loading visual
    btnText.innerText = 'Limpando...';
    spinner.style.display = 'block';
    btn.disabled = true;
    errorMsg.style.display = 'none';

    try {
        let [tab] = await chrome.tabs.query({ active: true, currentWindow: true });

        // TODO: Trocar para a URL do Render quando o deploy terminar
        const response = await fetch('https://cleanread-dti0.onrender.com/api/v1/articles/extract', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ url: tab.url })
        });

        if (!response.ok) throw new Error('Erro na API');

        const data = await response.json();

        await chrome.scripting.executeScript({
            target: { tabId: tab.id },
            func: renderCleanPage,
            args: [data]
        });

        window.close();

    } catch (error) {
        console.error(error);
        btnText.innerText = 'Tentar Novamente';
        spinner.style.display = 'none';
        btn.disabled = false;
        errorMsg.style.display = 'block';
    }
});

// Contexto do site original
function renderCleanPage(articleData) {
    document.head.innerHTML = `
        <title>${articleData.title} | CleanRead</title>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <style>
            /* Variáveis de Tema (Light Mode) */
            :root {
                --bg-color: #faf9f7;
                --text-primary: #1a1a1a;
                --text-secondary: #555555;
                --accent-color: #2563eb;
                --metrics-bg: #ffffff;
                --metrics-border: #e2e8f0;
                --font-sans: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
                --font-serif: 'Georgia', 'Cambria', 'Times New Roman', serif;
            }

            /* Variáveis de Tema (Dark Mode Automático) */
            @media (prefers-color-scheme: dark) {
                :root {
                    --bg-color: #121212;
                    --text-primary: #e5e5e5;
                    --text-secondary: #a3a3a3;
                    --accent-color: #60a5fa;
                    --metrics-bg: #1e1e1e;
                    --metrics-border: #333333;
                }
            }

            body {
                font-family: var(--font-serif);
                line-height: 1.8;
                color: var(--text-primary);
                background-color: var(--bg-color);
                margin: 0;
                padding: 40px 20px;
                font-size: 1.15rem;
                transition: background-color 0.3s, color 0.3s;
            }

            article {
                max-width: 680px; /* Largura ideal para leitura humana */
                margin: 0 auto;
            }

            h1 {
                font-family: var(--font-sans);
                font-size: 2.5rem;
                line-height: 1.2;
                font-weight: 800;
                margin-bottom: 0.5rem;
                letter-spacing: -0.02em;
            }

            .author {
                font-family: var(--font-sans);
                color: var(--text-secondary);
                font-size: 0.95rem;
                margin-bottom: 2rem;
                display: flex;
                align-items: center;
                gap: 8px;
            }

            /* Design do Card de Métricas */
            .metrics {
                background: var(--metrics-bg);
                padding: 16px 20px;
                border-radius: 12px;
                font-family: var(--font-sans);
                font-size: 0.9rem;
                color: var(--text-secondary);
                margin-bottom: 40px;
                border: 1px solid var(--metrics-border);
                box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
                display: flex;
                justify-content: space-between;
                align-items: center;
                flex-wrap: wrap;
                gap: 10px;
            }

            .metrics strong {
                color: var(--text-primary);
                font-size: 1rem;
                display: block;
                width: 100%;
                margin-bottom: 4px;
            }

            .metric-item { display: flex; align-items: center; gap: 6px; }

            .content p { margin-bottom: 1.8rem; word-wrap: break-word; }

            .content img {
                max-width: 100%;
                height: auto;
                border-radius: 8px;
                margin: 2rem 0;
                display: block;
                box-shadow: 0 4px 12px rgba(0,0,0,0.1);
            }

            .content a {
                color: var(--accent-color);
                text-decoration: underline;
                text-decoration-thickness: 1px;
                text-underline-offset: 4px;
            }

            .content a:hover { text-decoration-thickness: 2px; }

            .content h2, .content h3 {
                font-family: var(--font-sans);
                margin-top: 2.5rem;
                margin-bottom: 1rem;
                color: var(--text-primary);
            }
        </style>
    `;

    document.body.innerHTML = `
        <article>
            <h1>${articleData.title}</h1>
            <div class="author">✍️ Por: ${articleData.author}</div>
            
            <div class="metrics">
                <strong>⚡ CleanRead Metrics</strong>
                <div class="metric-item">⏱️ ${articleData.metrics.estimatedReadingTimeMinutes} min de leitura</div>
                <div class="metric-item">🗑️ ${articleData.metrics.removedElementsCount} elementos bloqueados</div>
            </div>

            <div class="content">
                ${articleData.content}
            </div>
        </article>
    `;
}