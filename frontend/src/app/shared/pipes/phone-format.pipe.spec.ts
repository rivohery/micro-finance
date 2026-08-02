import { PhoneFormatPipe } from './phone-format.pipe';

describe('PhoneFormatPipe', () => {
  let pipe: PhoneFormatPipe;

  // Avant chaque test, on crée une nouvelle instance de notre pipe
  beforeEach(() => {
    pipe = new PhoneFormatPipe();
  });

  it('devrait créer une instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('devrait formater correctement un numéro de 10 chiffres (ex: Malgache)', () => {
    const result = pipe.transform('0347366212');
    expect(result).toBe('034 73 662 12');
  });

  it('devrait supprimer les espaces superflus avant de formater', () => {
    const result = pipe.transform('034  73  66 212');
    expect(result).toBe('034 73 662 12');
  });

  it('devrait retourner une chaîne vide si la valeur est nulle ou indéfinie', () => {
    expect(pipe.transform(null)).toBe('');
    expect(pipe.transform(undefined)).toBe('');
  });

  it('devrait renvoyer la valeur initiale nettoyée si le numéro ne fait pas 10 chiffres', () => {
    expect(pipe.transform('12345')).toBe('12345');
  });
});
