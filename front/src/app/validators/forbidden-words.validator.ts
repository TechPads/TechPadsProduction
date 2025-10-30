import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

// Palabras y patrones prohibidos
const forbiddenPatterns = [
  /select/i,
  /insert/i,
  /update/i,
  /delete/i,
  /drop/i,
  /create/i,
  /alter/i,
  /exec/i,
  /script/i,
  /--/,
  /;/,
  /\/\*/,
  /\*\//,
  /union/i,
  / or /i,
  / and /i
];

export function forbiddenWordsValidator(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const value = control.value;
    if (!value || typeof value !== 'string') return null;

    for (const pattern of forbiddenPatterns) {
      if (pattern.test(value)) {
        return { forbiddenWord: true };
      }
    }

    return null;
  };
}
