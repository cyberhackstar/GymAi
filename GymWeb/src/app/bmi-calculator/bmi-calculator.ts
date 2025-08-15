import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

type UnitSystem = 'metric' | 'us';
type Gender = 'male' | 'female';
type BmrEq = 'mifflin' | 'harris' | 'katch';
type ActivityKey =
  | 'sedentary'
  | 'light'
  | 'moderate'
  | 'very_active'
  | 'extra_active';
type ToolKey = 'bmi' | 'bodyfat' | 'calories';

@Component({
  selector: 'app-bmi-calculator',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './bmi-calculator.html',
  styleUrls: ['./bmi-calculator.css'],
})
export class BmiCalculator {
  /* THEME (for any inline usage if needed) */
  readonly theme = {
    bg: '#0d0d0d',
    panel: '#121212',
    panel2: '#151515',
    text: '#e6e6e6',
    subtext: '#bdbdbd',
    border: '#242424',
    accent: '#ff4c4c',
    accentDim: 'rgba(255,76,76,.15)',
  };

  /* ------------------------------
   * GLOBAL UI STATE
   * ------------------------------ */
  unitSystem: UnitSystem = 'metric';
  currentTool: ToolKey = 'bmi';

  switchUnits(system: UnitSystem) {
    this.unitSystem = system;
  }
  selectTool(tool: ToolKey) {
    this.currentTool = tool;
  }

  /* ------------------------------
   * BMI CALCULATOR
   * ------------------------------ */
  heightCm?: number;
  weightKg?: number;

  heightFt?: number;
  heightIn?: number;
  weightLb?: number;

  bmiResult = '';
  bmiCategory = '';

  calculateBMI() {
    let bmi: number | null = null;

    if (this.unitSystem === 'metric') {
      if (
        !this.heightCm ||
        !this.weightKg ||
        this.heightCm <= 0 ||
        this.weightKg <= 0
      ) {
        alert('Please enter a valid height (cm) and weight (kg).');
        return;
      }
      const hM = this.heightCm / 100;
      bmi = this.weightKg / (hM * hM);
    } else {
      const totalIn =
        Number(this.heightFt || 0) * 12 + Number(this.heightIn || 0);
      if (!totalIn || !this.weightLb || totalIn <= 0 || this.weightLb <= 0) {
        alert('Please enter a valid height (ft/in) and weight (lb).');
        return;
      }
      bmi = 703 * (this.weightLb / (totalIn * totalIn));
    }

    this.bmiResult = (bmi as number).toFixed(1);
    const b = bmi as number;

    if (b < 18.5) this.bmiCategory = 'Underweight';
    else if (b < 25) this.bmiCategory = 'Healthy';
    else if (b < 30) this.bmiCategory = 'Overweight';
    else this.bmiCategory = 'Obese';
  }

  /* ------------------------------
   * BODY FAT (U.S. NAVY + BMI METHOD)
   * ------------------------------ */
  bfGender: Gender = 'male';
  bfAge?: number;

  // metric
  bfHeightCm?: number;
  bfNeckCm?: number;
  bfWaistCm?: number;
  bfHipCm?: number; // female only
  bfWeightKg?: number;

  // us
  bfHeightIn?: number;
  bfNeckIn?: number;
  bfWaistIn?: number;
  bfHipIn?: number; // female only
  bfWeightLb?: number;

  bodyFatNavy?: number;
  bodyFatCategory = '';
  bodyFatMassKg?: number;
  leanMassKg?: number;
  idealBfPercent?: number;
  bfToLoseKg?: number;
  bodyFatBmiMethod?: number;

  private aceCategory(g: Gender, bf: number): string {
    const ranges =
      g === 'female'
        ? [
            { label: 'Essential fat', min: 10, max: 13 },
            { label: 'Athletes', min: 14, max: 20 },
            { label: 'Fitness', min: 21, max: 24 },
            { label: 'Average', min: 25, max: 31 },
            { label: 'Obese', min: 32, max: 100 },
          ]
        : [
            { label: 'Essential fat', min: 2, max: 5 },
            { label: 'Athletes', min: 6, max: 13 },
            { label: 'Fitness', min: 14, max: 17 },
            { label: 'Average', min: 18, max: 24 },
            { label: 'Obese', min: 25, max: 100 },
          ];
    const found = ranges.find((r) => bf >= r.min && bf <= r.max);
    return found ? found.label : '—';
  }

  private jacksonPollockIdeal(g: Gender, age: number): number | undefined {
    const men: Record<number, number> = {
      20: 8.5,
      25: 10.5,
      30: 12.7,
      35: 13.7,
      40: 15.3,
      45: 16.4,
      50: 18.9,
      55: 20.9,
    };
    const women: Record<number, number> = {
      20: 17.7,
      25: 18.4,
      30: 19.3,
      35: 21.5,
      40: 22.2,
      45: 22.9,
      50: 25.2,
      55: 26.3,
    };
    const table = g === 'male' ? men : women;
    const ages = Object.keys(table).map(Number);
    const nearest = ages.reduce(
      (a, b) => (Math.abs(b - age) < Math.abs(a - age) ? b : a),
      ages[0]
    );
    return table[nearest];
  }

  private log10(x: number): number {
    return Math.log(x) / Math.LN10;
  }
  private toMetricLength(value: number, system: UnitSystem): number {
    return system === 'metric' ? value : value * 2.54; // inches -> cm
  }
  private toMetricWeight(value: number, system: UnitSystem): number {
    return system === 'metric' ? value : value * 0.45359237; // lb -> kg
  }

  calculateBodyFat() {
    const system: UnitSystem = this.unitSystem;

    const heightCm =
      system === 'metric'
        ? this.bfHeightCm
        : this.bfHeightIn != null
        ? this.toMetricLength(this.bfHeightIn, 'us')
        : undefined;

    const neckCm =
      system === 'metric'
        ? this.bfNeckCm
        : this.bfNeckIn != null
        ? this.toMetricLength(this.bfNeckIn, 'us')
        : undefined;

    const waistCm =
      system === 'metric'
        ? this.bfWaistCm
        : this.bfWaistIn != null
        ? this.toMetricLength(this.bfWaistIn, 'us')
        : undefined;

    const hipCm =
      system === 'metric'
        ? this.bfHipCm
        : this.bfHipIn != null
        ? this.toMetricLength(this.bfHipIn, 'us')
        : undefined;

    const weightKg =
      system === 'metric'
        ? this.bfWeightKg
        : this.bfWeightLb != null
        ? this.toMetricWeight(this.bfWeightLb, 'us')
        : undefined;

    if (
      !heightCm ||
      !neckCm ||
      !waistCm ||
      !weightKg ||
      (this.bfGender === 'female' && !hipCm)
    ) {
      alert('Please fill all required measurements for your gender.');
      return;
    }
    if (!this.bfAge || this.bfAge < 15 || this.bfAge > 80) {
      alert('Please provide a valid age (15–80).');
      return;
    }

    let navy: number;
    if (this.bfGender === 'male') {
      const x = waistCm - neckCm;
      if (x <= 0) {
        alert('Waist must be larger than neck.');
        return;
      }
      navy =
        495 /
          (1.0324 - 0.19077 * this.log10(x) + 0.15456 * this.log10(heightCm)) -
        450;
    } else {
      const x = waistCm + (hipCm as number) - neckCm;
      if (x <= 0) {
        alert('Waist + hip must be larger than neck.');
        return;
      }
      navy =
        495 /
          (1.29579 - 0.35004 * this.log10(x) + 0.221 * this.log10(heightCm)) -
        450;
    }
    this.bodyFatNavy = Number(navy.toFixed(1));

    // BMI method (Deurenberg)
    const hM = heightCm / 100;
    const bmiVal = weightKg / (hM * hM);
    const sexNum = this.bfGender === 'male' ? 1 : 0;
    this.bodyFatBmiMethod = Number(
      (1.2 * bmiVal + 0.23 * this.bfAge - 10.8 * sexNum - 5.4).toFixed(1)
    );

    // Derived values
    this.bodyFatCategory = this.aceCategory(this.bfGender, this.bodyFatNavy);
    this.bodyFatMassKg = Number(
      ((this.bodyFatNavy / 100) * weightKg).toFixed(1)
    );
    this.leanMassKg = Number((weightKg - (this.bodyFatMassKg || 0)).toFixed(1));
    this.idealBfPercent = this.jacksonPollockIdeal(this.bfGender, this.bfAge);

    if (this.idealBfPercent != null) {
      const idealFatMass = weightKg * (this.idealBfPercent / 100);
      this.bfToLoseKg = Number(
        ((this.bodyFatMassKg as number) - idealFatMass).toFixed(1)
      );
      if ((this.bfToLoseKg as number) < 0) this.bfToLoseKg = 0;
    }
  }

  /* ------------------------------
   * CALORIES (BMR / TDEE)
   * ------------------------------ */
  calGender: Gender = 'male';
  calAge?: number;

  calHeightCm?: number;
  calWeightKg?: number;

  calHeightFt?: number;
  calHeightIn?: number;
  calWeightLb?: number;

  calActivity: ActivityKey = 'moderate';
  calEq: BmrEq = 'mifflin';
  calBmr?: number;
  calTdee?: number;
  calCut?: number;
  calBulk?: number;

  private activityFactors: Record<ActivityKey, number> = {
    sedentary: 1.2,
    light: 1.375,
    moderate: 1.55,
    very_active: 1.725,
    extra_active: 1.9,
  };

  calculateCalories() {
    let W: number | undefined;
    let H: number | undefined;

    if (this.unitSystem === 'metric') {
      W = this.calWeightKg;
      H = this.calHeightCm;
    } else {
      if (this.calWeightLb != null)
        W = this.toMetricWeight(this.calWeightLb, 'us');
      const totalIn =
        Number(this.calHeightFt || 0) * 12 + Number(this.calHeightIn || 0);
      if (totalIn) H = this.toMetricLength(totalIn, 'us');
    }

    if (!W || !H || !this.calAge) {
      alert('Please provide age, height, and weight.');
      return;
    }

    let BMR: number;
    if (this.calEq === 'mifflin') {
      BMR =
        10 * W +
        6.25 * H -
        5 * this.calAge +
        (this.calGender === 'male' ? 5 : -161);
    } else if (this.calEq === 'harris') {
      BMR =
        this.calGender === 'male'
          ? 13.397 * W + 4.799 * H - 5.677 * this.calAge + 88.362
          : 9.247 * W + 3.098 * H - 4.33 * this.calAge + 447.593;
    } else {
      const bf = this.bodyFatNavy ?? this.bodyFatBmiMethod;
      if (bf == null) {
        alert(
          'Katch-McArdle requires body fat %. Calculate Body Fat first or switch equation.'
        );
        return;
      }
      const F = bf / 100;
      BMR = 370 + 21.6 * (1 - F) * W;
    }

    const factor = this.activityFactors[this.calActivity];
    const TDEE = BMR * factor;

    this.calBmr = Math.round(BMR);
    this.calTdee = Math.round(TDEE);
    this.calCut = Math.round(TDEE - 500);
    this.calBulk = Math.round(TDEE + 500);
  }
}
