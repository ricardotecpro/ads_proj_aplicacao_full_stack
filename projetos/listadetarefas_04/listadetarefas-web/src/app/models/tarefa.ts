export interface Tarefa {
  id?: number;
  descricao: string;
  concluida: boolean;
  selecionada?: boolean; // Propriedade apenas do frontend
}
