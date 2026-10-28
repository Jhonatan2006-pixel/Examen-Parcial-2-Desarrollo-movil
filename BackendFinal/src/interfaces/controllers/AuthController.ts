import { Request, Response } from "express";
import bcrypt from "bcryptjs";

import { respuestaInicioSesion } from "../../application_use_cases/IniciarSesion";
import { UsuarioRepository } from "../../infrastructure/repositories/UsuarioRepository";

const usuarioRepository = new UsuarioRepository();

export class AuthController {

    // ============================
    // REGISTRO
    // ============================
    static async register(req: Request, res: Response) {

        try {

            const {
                nombre,
                email,
                telefono,
                password
            } = req.body;

            // Validar campos obligatorios
            if (!nombre || !email || !password) {
                return res.status(400).json({
                    success: false,
                    message: "Nombre, correo y contraseña son obligatorios",
                    data: null
                });
            }

            // Verificar si el correo ya existe
            const usuarioExistente =
                await usuarioRepository.findByEmail(email);

            if (usuarioExistente) {
                return res.status(409).json({
                    success: false,
                    message: "El correo ya está registrado",
                    data: null
                });
            }

            // Encriptar contraseña
            const passwordHash =
                await bcrypt.hash(password, 10);

            // Crear usuario
            const nuevoUsuario =
                await usuarioRepository.create({
                    nombre,
                    email,
                    telefono,
                    password: passwordHash
                });

            return res.status(201).json({
                success: true,
                message: "Usuario registrado correctamente",
                data: {
                    id: nuevoUsuario.id,
                    nombre: nuevoUsuario.nombre,
                    email: nuevoUsuario.email,
                    telefono: nuevoUsuario.telefono
                }
            });

        } catch (error) {

            console.error("Error en register:", error);

            return res.status(500).json({
                success: false,
                message: "Error interno del servidor",
                data: null
            });
        }
    }


    // ============================
    // LOGIN
    // ============================
    static async login(req: Request, res: Response) {

        try {

            const {
                email,
                password
            } = req.body;

            // Validar campos obligatorios
            if (!email || !password) {
                return res.status(400).json({
                    success: false,
                    message: "Correo y contraseña son obligatorios",
                    data: null
                });
            }

            // Buscar usuario por correo
            const usuario =
                await usuarioRepository.findByEmail(email);

            if (!usuario) {
                return res.status(401).json({
                    success: false,
                    message: "Correo o contraseña incorrectos",
                    data: null
                });
            }

            // Comparar contraseña con el hash guardado
            const passwordCorrecta =
                await bcrypt.compare(password, usuario.password);

            if (!passwordCorrecta) {
                return res.status(401).json({
                    success: false,
                    message: "Correo o contraseña incorrectos",
                    data: null
                });
            }

            // Login correcto: token JWT y datos del usuario
            return res.status(200).json(respuestaInicioSesion(usuario));

        } catch (error) {

            console.error("Error en login:", error);

            return res.status(500).json({
                success: false,
                message: "Error interno del servidor",
                data: null
            });
        }
    }


    // ============================
    // PERFIL
    // ============================
    static async profile(req: Request, res: Response) {

        try {

            const user = (req as any).user;

            if (!user || !user.id) {
                return res.status(401).json({
                    success: false,
                    message: "Usuario no autenticado",
                    data: null
                });
            }

            const usuario =
                await usuarioRepository.findById(user.id);

            if (!usuario) {
                return res.status(404).json({
                    success: false,
                    message: "Usuario no encontrado",
                    data: null
                });
            }

            return res.status(200).json({
                success: true,
                message: "Perfil obtenido correctamente",
                data: {
                    id: usuario.id,
                    nombre: usuario.nombre,
                    email: usuario.email,
                    telefono: usuario.telefono
                }
            });

        } catch (error) {

            console.error("Error en profile:", error);

            return res.status(500).json({
                success: false,
                message: "Error interno del servidor",
                data: null
            });
        }
    }
}
