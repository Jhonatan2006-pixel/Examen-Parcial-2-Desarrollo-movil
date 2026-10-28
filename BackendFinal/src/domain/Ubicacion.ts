export interface Ubicacion {
    id?: number;
    usuario_id: number;
    nombre: string;
    descripcion: string;
    latitud: number;
    longitud: number;
    created_at?: Date;
}
