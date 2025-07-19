import { Injectable } from "@angular/core";

@Injectable({ providedIn: 'root' })
export class ColorService {
   private readonly acccentColors: string[] = [
      '#5b7ae5', // синий
      '#f1ba62', // оранжевый
      '#ca67fb', // фиолетовый
      '#4be565', // зелёный
      '#fb6767', // красный
      '#5dccf1', // голубой
      '#e4c412', // жёлтый
      '#fb67d8', // розовый
      '#52ddc5', // бирюзовый
   ];
   private readonly backgroundColors: string[] = [
      '#e2e5f0', // синий
      '#f3eee7', // оранжевый
      '#e7deed', // фиолетовый
      '#dfede1', // зелёный
      '#f0e5e5', // красный
      '#e1eaed', // голубой
      '#f3f0e4', // жёлтый
      '#f0e3ec', // розовый 
      '#dbe8e6', // бирюзовый
   ];

   public getAccentColor(id: number):string {
      return this.acccentColors[id % this.acccentColors.length];
   }

   public getBackgroundColor(id: number):string {
      return this.backgroundColors[id % this.backgroundColors.length];
   }
}