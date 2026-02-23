package br.com.aula.gestaodeestoques.mapper;

import br.com.aula.gestaodeestoques.dto.FornecedorDTO;
import br.com.aula.gestaodeestoques.model.Fornecedor;
import org.springframework.stereotype.Component;

@Component
public class FornecedorMapper {

    /**
     * Converte a entidade (Model) do banco de dados para o Objeto de Transferência de Dados (DTO).
     * Usado para enviar dados para fora da API.
     * @param fornecedor a entidade vinda do banco.
     * @return um DTO com os dados a serem expostos.
     */
    public FornecedorDTO toDTO(Fornecedor fornecedor) {
        if (fornecedor == null) {
            return null;
        }
        return new FornecedorDTO(
                fornecedor.id(),
                fornecedor.nome(),
                fornecedor.cnpj()
        );
    }

    /**
     * Converte o Objeto de Transferência de Dados (DTO) para a Entidade (Model) do banco.
     * Usado ao receber dados na API (ex: em uma criação ou atualização).
     * @param fornecedorDTO o DTO recebido na requisição.
     * @return uma entidade pronta para ser salva no banco.
     */
    public Fornecedor toEntity(FornecedorDTO fornecedorDTO) {
        if (fornecedorDTO == null) {
            return null;
        }
        return new Fornecedor(
                fornecedorDTO.id(),
                fornecedorDTO.nome(),
                fornecedorDTO.cnpj()
        );
    }
}