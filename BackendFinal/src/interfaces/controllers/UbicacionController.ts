import { Request, Response } from "express";
import { UbicacionRepository } from "../../infrastructure/repositories/UbicacionRepository";

const ubicacionRepository = new UbicacionRepository();

// Convierte el valor recibido en un número válido o null
const comoNumero = (valor: unknown): number | null => {
    if (valor === undefined || valor === null || valor === "") {
        return null;
    }

    const numero = Number(valor);

    return Number.isFinite(numero) ? numero : null;
};

export class UbicacionController {

    // ==========================================
    // GUARDAR UBICACIÓN DEL USUARIO AUTENTICADO
    // ==========================================
    static async crear(req: Request, res: Response) {

        try {

            // Usuario obtenido desde el token JWT
            const user = (req as any).user;

            if (!user || !user.id) {
                return res.status(401).json({
                    success: false,
                    message: "Usuario no autenticado",
                    data: null
                });
            }

            const nombre = typeof req.body.nombre === "string"
                ? req.body.nombre.trim()
                : "";

            const descripcion = typeof req.body.descripcion === "string"
                ? req.body.descripcion.trim()
                : "";

            const latitud = comoNumero(req.body.latitud);
            const longitud = comoNumero(req.body.longitud);

            // Validar campos obligatorios
            if (!nombre || !descripcion || latitud === null || longitud === null) {
                return res.status(400).json({
                    success: false,
                    message: "Nombre, descripción, latitud y longitud son obligatorios",
                    data: null
                });
            }

            if (nombre.length > 100 || descripcion.length > 500) {
                return res.status(400).json({
                    success: false,
                    message: "El nombre admite 100 caracteres y la descripción 500",
                    data: null
                });
            }

            // Validar que las coordenadas existan en el planeta
            if (latitud < -90 || latitud > 90 || longitud < -180 || longitud > 180) {
                return res.status(400).json({
                    success: false,
                    message: "Las coordenadas no son válidas",
                    data: null
                });
            }

            const nuevaUbicacion = await ubicacionRepository.create({
                usuario_id: user.id,
                nombre,
                descripcion,
                latitud,
                longitud
            });

            return res.status(201).json({
                success: true,
                message: "Ubicación guardada correctamente",
                data: nuevaUbicacion
            });

        } catch (error) {

            console.error("Error al guardar ubicación:", error);

            return res.status(500).json({
                success: false,
                message: "Error interno al guardar la ubicación",
                data: null
            });
        }
    }


    // ==========================================
    // OBTENER UBICACIONES DEL USUARIO AUTENTICADO
    // ==========================================
    static async obtenerMias(req: Request, res: Response) {

        try {

            const user = (req as any).user;

            if (!user || !user.id) {
                return res.status(401).json({
                    success: false,
                    message: "Usuario no autenticado",
                    data: null
                });
            }

            const ubicaciones = await ubicacionRepository.findByUsuario(user.id);

            return res.status(200).json({
                success: true,
                message: "Ubicaciones obtenidas correctamente",
                data: ubicaciones
            });

        } catch (error) {

            console.error("Error al obtener ubicaciones:", error);

            return res.status(500).json({
                success: false,
                message: "Error interno al obtener las ubicaciones",
                data: null
            });
        }
    }
}
