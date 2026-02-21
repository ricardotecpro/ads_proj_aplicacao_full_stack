export interface Tarefa {
  id: number;
  titulo: string;
  descricao: string;
  concluida: boolean;
  dataCriacao: string; // O JSON converte LocalDateTime para String
  dataAtualizacao: string;
}
