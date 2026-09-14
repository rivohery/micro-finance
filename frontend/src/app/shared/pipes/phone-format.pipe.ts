import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'phoneFormat',
})
export class PhoneFormatPipe implements PipeTransform {
  transform(value: string | number | null | undefined): string {
    if (!value) {
      return '';
    }
    const phoneStr = value.toString().replace(/\s+/g, '');
    if (phoneStr.length === 10) {
      const part1 = phoneStr.substring(0, 3);
      const part2 = phoneStr.substring(3, 5);
      const part3 = phoneStr.substring(5, 8);
      const part4 = phoneStr.substring(8, 10);

      return `${part1} ${part2} ${part3} ${part4}`;
    }
    // Si le format n'est pas standard (pas 10 chiffres), on retourne la valeur nettoyée
    return phoneStr;
  }
}
