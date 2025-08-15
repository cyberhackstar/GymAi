export interface UserProfile {
  userId?: number; // ID from auth-service (will come from JWT)
  name?: string;
  email?: string;
  age: number;
  height: number;
  weight: number;
  gender: string;
  goal: string;
  activityLevel: string;
  preference: string;
}
