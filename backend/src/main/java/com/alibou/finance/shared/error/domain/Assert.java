package com.alibou.finance.shared.error.domain;

/**
 * Classe utilitaire pour la validation classique
 */
public class Assert {


    public static void notNull(String name,Object value){
        if(value == null){
            throw new IllegalArgumentException(name + " ne doit pas être null");
        }
    }

    //Point d'entrée pour le chaînage (ex: Assert.field(...).positive())
    public static FieldValidator field(String name, Object value){
        return new FieldValidator(name, value);
    }

    // Classe interne pour gérer les validations à la chaîne
    public static class FieldValidator{
        private  final String name;
        private  final Object value;

        private FieldValidator(String name, Object value){
            this.name = name;
            this.value = value;
            //On vérifie le null en premier par sécurité
            notNull(name, value);
        }

        public FieldValidator isNumber(){
            if(!(value instanceof Number)){
                throw new IllegalArgumentException(name + " doit être un nombre valide");
            }
            return this;
        }

        public  FieldValidator positiveStrict(){
            if(value instanceof Number num  && num.doubleValue() <= 0){
                throw new IllegalArgumentException(name + " doit être strictement positive");
            }
            return this;
        }

        public  FieldValidator positive(){
            if(value instanceof Number num  && num.doubleValue() < 0){
                throw new IllegalArgumentException(name + " doit être positive");
            }
            return this;
        }

        public FieldValidator min(double min){
            if(value instanceof Number num && num.doubleValue() < min){
                throw new IllegalArgumentException(name + " ne doit pas inférieur à la valeur minimal: " + min);
            }
            return this;
        }

        public FieldValidator max(double max){
            if(value instanceof Number num && num.doubleValue() > max){
                throw new IllegalArgumentException(name + " ne doit pas dépasser la valeur maximal: " + max);
            }
            return this;
        }

        public FieldValidator notEmpty(){
            if(value instanceof String s && s.isBlank()){
                throw new IllegalArgumentException(name + " ne doit pas vide");
            }
            return this;
        }

        public FieldValidator minLength(int min){
            if(value instanceof String s && s.length() < min){
                throw new IllegalArgumentException(name + " est trop court (min " + min + " caractères)");
            }
            return this;
        }

        public FieldValidator maxLength(int max){
            if(value instanceof String s && s.length() > max){
                throw new IllegalArgumentException(name + " est trop long (max " + max + " caractères)");
            }
            return this;
        }

        public FieldValidator between(int min, int max){
            if(value instanceof String s && (s.length() < min || s.length() > max)){
                throw new IllegalArgumentException(name + " doit être compris entre (min " + min + " et max " + max +")");
            }
            return this;
        }

        public FieldValidator size(int size){
            if(value instanceof String s && s.length() != size){
                throw new IllegalArgumentException(name + " doit avoir exactement " + size + " caractères");
            }
            return this;
        }


    }


}
