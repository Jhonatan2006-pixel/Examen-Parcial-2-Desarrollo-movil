import { Request, Response, NextFunction } from "express";
import jwt from "jsonwebtoken";

// Valida el header "Authorization: Bearer <token>" y deja
// los datos del usuario del token en req.user
export const authMiddleware = (
  req: Request,
  res: Response,
  next: NextFunction
): void => {
  const authHeader = req.headers.authorization;

  if (!authHeader) {
    res.status(401).json({
      success: false,
      message: "Token no proporcionado",
      data: null
    });
    return;
  }

  const [tipo, token] = authHeader.split(" ");

  if (tipo !== "Bearer" || !token) {
    res.status(401).json({
      success: false,
      message: "Formato de token inválido",
      data: null
    });
    return;
  }

  try {
    const decoded = jwt.verify(token, process.env.JWT_SECRET as string);
    (req as any).user = decoded;
    next();
  } catch (error) {
    res.status(401).json({
      success: false,
      message: "Token inválido o expirado",
      data: null
    });
  }
};
