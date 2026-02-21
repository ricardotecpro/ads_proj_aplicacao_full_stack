export interface Tarefa {
  id?: number; // Opcional ao criar, presente ao receber da API.
  descricao: string;
  concluida: boolean;
  editando?: boolean; // Propriedade de controle de UI, não existe no backend.
}
