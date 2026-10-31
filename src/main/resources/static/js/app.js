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
            row.querySelectorAll('[data-medicine-field]').forEach(input => {
                input.name = `additionalMedicines[${index}].${input.dataset.medicineField}`;
                input.id = `additionalMedicines${index}.${input.dataset.medicineField}`;
                input.closest('.field').querySelector('label').htmlFor = input.id;
            });
        });
        addMedicine.addEventListener('click', () => {
            const row = document.createElement('fieldset');
            row.className = 'additional-medicine';
            const legend = document.createElement('legend');
            legend.textContent = '追加の薬剤';
            const controls = document.createElement('div');
            controls.className = 'form-grid';
            for (const [name, title, maxLength] of [
                ['medicineName', '薬剤名', 200], ['usage', '用法', 200],
                ['dosage', '用量', 100], ['durationDays', '服用日数', null]
            ]) {
                const field = document.createElement('div');
                field.className = 'field';
                const label = document.createElement('label');
                label.textContent = `${title}（必須）`;
                const input = document.createElement('input');
                input.dataset.medicineField = name;
                input.required = true;
                input.type = maxLength ? 'text' : 'number';
                if (maxLength) {
                    input.maxLength = maxLength;
                    validateRequiredText(input);
                } else {
                    input.min = '1';
                    input.max = '2147483647';
                    input.step = '1';
                }
                field.append(label, input);
                controls.append(field);
            }
            const remove = document.createElement('button');
            remove.type = 'button';
            remove.className = 'button secondary small';
            remove.dataset.removeMedicine = '';
            remove.textContent = '削除';
            row.append(legend, controls, remove);
            medicines.append(row);
            reindex();
            row.querySelector('input').focus();
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
