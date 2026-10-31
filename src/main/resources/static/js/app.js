'use strict';
// クライアントの補助機能。最終的な検証はSpring Validationが行います。
document.addEventListener('DOMContentLoaded', () => {
    const date = new Date();
    const today = [date.getFullYear(), String(date.getMonth() + 1).padStart(2, '0'), String(date.getDate()).padStart(2, '0')].join('-');
    document.querySelectorAll('[data-birth-date]').forEach(input => { input.max = today; });
    document.querySelectorAll('nav a').forEach(link => {
        if (link.pathname === location.pathname && link.search === location.search) {
            link.setAttribute('aria-current', 'page');
        }
    });
    // 空白のみの必須文字列を送信する前に通知します。
    document.querySelectorAll('input[required][type="text"], textarea[required]').forEach(input => {
        const validate = () => input.setCustomValidity(input.value.trim() ? '' : '空白以外の文字を入力してください');
        input.addEventListener('input', validate);
        input.addEventListener('invalid', validate);
    });
});
