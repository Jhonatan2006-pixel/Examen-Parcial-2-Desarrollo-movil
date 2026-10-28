import UbicacionModel from "../models/UbicacionModel";
import { Ubicacion } from "../../domain/Ubicacion";

export class UbicacionRepository {

    // Guardar una nueva ubicación del usuario
    async create(ubicacion: Ubicacion) {
        return await UbicacionModel.create({
            usuario_id: ubicacion.usuario_id,
            nombre: ubicacion.nombre,
            descripcion: ubicacion.descripcion,
            latitud: ubicacion.latitud,
            longitud: ubicacion.longitud
        });
    }

    // Todas las ubicaciones de un usuario, de la más reciente a la más antigua
    async findByUsuario(usuarioId: number) {
        return await UbicacionModel.findAll({
            where: {
                usuario_id: usuarioId
            },
            order: [["created_at", "DESC"], ["id", "DESC"]]
        });
    }
}
