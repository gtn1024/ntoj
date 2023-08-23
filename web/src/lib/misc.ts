export function toFixedNumber(score: number, decimalPlaces: number = 2): number {
  return Number.parseFloat(score.toFixed(decimalPlaces))
}
