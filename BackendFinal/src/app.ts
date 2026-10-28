import express from "express";
import cors from "cors";

import authRoutes from "./interfaces/routes/authRoutes";
import ubicacionRoutes from "./interfaces/routes/ubicacionRoutes";

const app = express();

app.use(cors());
app.use(express.json());

app.use("/api/auth", authRoutes);
app.use("/api/ubicaciones", ubicacionRoutes);

export default app;
