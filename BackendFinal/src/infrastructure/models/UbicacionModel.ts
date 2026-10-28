import { DataTypes, Model } from "sequelize";
import { sequelize } from "../database/sequelize";

// Ubicación de interés capturada por un usuario desde la app GeoTracker
class UbicacionModel extends Model {
    public id!: number;
    public usuario_id!: number;
    public nombre!: string;
    public descripcion!: string;
    public latitud!: number;
    public longitud!: number;
    public created_at!: Date;
}

UbicacionModel.init(
    {
        id: {
            type: DataTypes.INTEGER,
            autoIncrement: true,
            primaryKey: true
        },

        usuario_id: {
            type: DataTypes.INTEGER,
            allowNull: false
        },

        nombre: {
            type: DataTypes.STRING(100),
            allowNull: false
        },

        descripcion: {
            type: DataTypes.STRING(500),
            allowNull: false
        },

        // PostgreSQL devuelve los DECIMAL como texto y SQL Server como
        // número: el get() hace que la API siempre responda un número
        latitud: {
            type: DataTypes.DECIMAL(10, 7),
            allowNull: false,
            get() {
                return Number(this.getDataValue("latitud"));
            }
        },

        longitud: {
            type: DataTypes.DECIMAL(10, 7),
            allowNull: false,
            get() {
                return Number(this.getDataValue("longitud"));
            }
        },

        created_at: {
            type: DataTypes.DATE,
            allowNull: false,
            defaultValue: DataTypes.NOW
        }
    },
    {
        sequelize,
        tableName: "ubicaciones",
        schema: "dbo",
        timestamps: false
    }
);

export default UbicacionModel;
