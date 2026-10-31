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
    const validateRequiredText = input => {
        const validate = () => input.setCustomValidity(input.value.trim() ? '' : '空白以外の文字を入力してください');
        input.addEventListener('input', validate);
        input.addEventListener('invalid', validate);
    };
    document.querySelectorAll('input[required][type="text"], textarea[required]').forEach(validateRequiredText);

    const medicines = document.getElementById('additional-medicines');
    const addMedicine = document.getElementById('add-medicine');
    if (medicines && addMedicine) {
        const reindex = () => medicines.querySelectorAll('.additional-medicine').forEach((row, index) => {
            const input = row.querySelector('input');
            input.name = `additionalMedicineNames[${index}]`;
            input.id = `additionalMedicineNames${index}`;
            row.querySelector('label').htmlFor = input.id;
        });
        addMedicine.addEventListener('click', () => {
            const row = document.createElement('div');
            row.className = 'additional-medicine';
            const label = document.createElement('label');
            label.textContent = '追加の薬剤名（必須）';
            const controls = document.createElement('div');
            controls.className = 'search-row';
            const input = document.createElement('input');
            input.type = 'text';
            input.maxLength = 200;
            input.required = true;
            validateRequiredText(input);
            const remove = document.createElement('button');
            remove.type = 'button';
            remove.className = 'button secondary small';
            remove.dataset.removeMedicine = '';
            remove.textContent = '削除';
            controls.append(input, remove);
            row.append(label, controls);
            medicines.append(row);
            reindex();
            input.focus();
        });
        medicines.addEventListener('click', event => {
            const remove = event.target.closest('[data-remove-medicine]');
            if (!remove) return;
            remove.closest('.additional-medicine').remove();
            reindex();
            addMedicine.focus();
        });
    }
});
