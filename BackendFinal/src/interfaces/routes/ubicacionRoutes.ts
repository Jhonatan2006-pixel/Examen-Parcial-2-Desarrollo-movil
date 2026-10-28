import { Router } from "express";
import { UbicacionController } from "../controllers/UbicacionController";
import { authMiddleware } from "../middlewares/authMiddleware";

const router = Router();

// Las dos rutas exigen un token JWT válido: cada usuario
// guarda y consulta únicamente sus propias ubicaciones
router.post("/", authMiddleware, UbicacionController.crear);
router.get("/", authMiddleware, UbicacionController.obtenerMias);

export default router;
