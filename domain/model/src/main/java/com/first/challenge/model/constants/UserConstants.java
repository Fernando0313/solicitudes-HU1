package com.first.challenge.model.constants;

public final class UserConstants {

    public static final int NOMBRE_MAX_LENGTH = 100;
    public static final int APELLIDO_MAX_LENGTH = 100;
    public static final int EMAIL_MAX_LENGTH = 255;
    public static final int TELEFONO_MAX_LENGTH = 20;
    public static final int DOCUMENTO_MAX_LENGTH = 20;
    public static final String TELEFONO_PATTERN = "^[0-9+\\-\\s()]*$";
    public static final String DOCUMENTO_PATTERN = "^[0-9]{8,20}$";
    public static final String CARACTERES_SUFIJO = " caracteres";
    public static final String NOMBRE_REQUERIDO = "El campo 'nombre' es obligatorio";
    public static final String APELLIDO_REQUERIDO = "El campo 'apellido' es obligatorio";
    public static final String EMAIL_REQUERIDO = "El campo 'email' es obligatorio";
    public static final String EMAIL_FORMATO_INVALIDO = "El formato del email no es válido";
    public static final String NOMBRE_TAMANO_INVALIDO = "El nombre no puede exceder " + NOMBRE_MAX_LENGTH + CARACTERES_SUFIJO;
    public static final String APELLIDO_TAMANO_INVALIDO = "El apellido no puede exceder " + APELLIDO_MAX_LENGTH + CARACTERES_SUFIJO;
    public static final String EMAIL_TAMANO_INVALIDO = "El email no puede exceder " + EMAIL_MAX_LENGTH + CARACTERES_SUFIJO;
    public static final String TELEFONO_FORMATO_INVALIDO = "El teléfono solo puede contener números, espacios y los caracteres +, -, (, )";
    public static final String DOCUMENTO_FORMATO_INVALIDO = "El documento de identidad debe contener solo números y tener entre 8 y 20 dígitos";
    public static final String SALARIO_MINIMO_INVALIDO = "El salario base debe ser mayor a 0";
    public static final String SALARIO_BASE_REQUERIDO = "El campo 'salario_base' es obligatorio";
    public static final String SALARIO_MAXIMO_INVALIDO = "El salario base no puede exceder 15,000,000";
    public static final String SALARIO_FORMATO_INVALIDO = "El salario base debe tener máximo 10 dígitos enteros y 2 decimales";
    public static final String ROL_REQUERIDO = "El ID del rol es obligatorio";
    public static final String ROL_POSITIVO_REQUERIDO = "El id_rol debe ser un número positivo";
    public static final String USUARIO_YA_EXISTE = "El usuario con email %s ya se encuentra registrado";
    public static final String CONTRASENA_OBLIGATORIA = "La contraseña es obligatoria";
    public static final String CONTRASENA_TAMANO_INVALIDO = "La contraseña debe tener entre 6 y 100 caracteres";
    public static final String DOCUMENTO_REQUERIDO = "El campo 'documento_identidad' es obligatorio";
    public static final String DIRECCION_MAX_LENGTH = "200";
    public static final String DIRECCION_TAMANO_INVALIDO = "La dirección no puede exceder " + DIRECCION_MAX_LENGTH + CARACTERES_SUFIJO;
    public static final String PASSWORD_REQUERIDO = "La contraseña es obligatoria";
    public static final String PASSWORD_TAMANO_INVALIDO = "La contraseña debe tener entre 6 y 100 caracteres";


    private UserConstants() {
    }
}
