<template>
  <div class="dashboard-wrapper">
    <div class="background-pattern"></div>
    
    <!-- Header -->
    <header class="dashboard-header">
      <div class="header-content">
        <div class="header-left">
          <div class="logo">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M20 7L9 18L4 13" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            <span>Categorias</span>
          </div>
        </div>
        <div class="header-right">
          <button class="user-menu" @click="voltarDashboard">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M19 12H5M12 19L5 12L12 5" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            Voltar
          </button>
        </div>
      </div>
    </header>

    <div class="dashboard-container">
      <div class="dashboard-header-section">
        <h1 class="dashboard-title">Gerenciar Categorias</h1>
        
        <!-- Botão de Toggle dos Filtros -->
        <button class="filter-toggle-btn" @click="toggleFiltros" :class="{ active: mostrarFiltros }">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <polygon points="22,3 2,3 10,12.46 10,19 14,21 14,12.46" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          <span>{{ mostrarFiltros ? 'Ocultar Filtros' : 'Mostrar Filtros' }}</span>
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" class="chevron" :class="{ rotated: mostrarFiltros }">
            <polyline points="6,9 12,15 18,9" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </button>
      </div>

      <div v-if="erro" class="erro-alert">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/>
          <line x1="15" y1="9" x2="9" y2="15" stroke="currentColor" stroke-width="2"/>
          <line x1="9" y1="9" x2="15" y2="15" stroke="currentColor" stroke-width="2"/>
        </svg>
        {{ erro }}
        <button @click="erro = ''" class="close-error">×</button>
      </div>

      <div v-if="sucesso" class="sucesso-alert">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          <polyline points="22,4 12,14.01 9,11.01" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
        {{ sucesso }}
        <button @click="sucesso = ''" class="close-error">×</button>
      </div>

      <!-- Filtros Colapsáveis -->
      <transition name="filter-slide">
        <div v-show="mostrarFiltros" class="filtros-card">
          <h3 class="filtros-title">Filtros</h3>
          <div class="filtros-content">
            <div class="filtro-group">
              <label>Nome da Categoria</label>
              <input type="text" v-model="filtroNome" placeholder="Buscar categoria..." class="text-input" />
            </div>
            <div class="filtro-group">
              <label>Tipo</label>
              <select v-model="filtroTipo" class="select-input">
                <option value="">Todas</option>
                <option value="RECEITA">Receitas</option>
                <option value="DESPESA">Despesas</option>
              </select>
            </div>
            <div class="filtro-group">
              <label>Status</label>
              <select v-model="filtroAtiva" class="select-input">
                <option value="">Todas</option>
                <option value="true">Ativas</option>
                <option value="false">Inativas</option>
              </select>
            </div>
            <button @click="carregarCategorias" class="filtrar-button" :disabled="carregando">
              <span v-if="carregando" class="loading-spinner"></span>
              <svg v-else width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <circle cx="11" cy="11" r="8" stroke="currentColor" stroke-width="2"/>
                <path d="M21 21L16.65 16.65" stroke="currentColor" stroke-width="2"/>
              </svg>
              {{ carregando ? 'Carregando...' : 'Filtrar' }}
            </button>
          </div>
        </div>
      </transition>

      <!-- Categorias -->
      <div class="transacoes-card">
        <div class="transacoes-header">
          <h3>Categorias ({{ totalCategorias }} encontradas)</h3>
          
          <div class="header-actions">
            <button class="add-transaction-btn" @click="abrirModalCategoria()">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <line x1="12" y1="5" x2="12" y2="19" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <line x1="5" y1="12" x2="19" y2="12" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
              Nova Categoria
            </button>
          </div>
        </div>

        <div v-if="categorias.length" class="transacoes-table">
          <!-- Versão Desktop - Tabela Tradicional -->
          <div class="desktop-table">
            <div class="table-header">
              <div class="th">Nome</div>
              <div class="th">Tipo</div>
              <div class="th">Status</div>
              <div class="th">Ações</div>
            </div>
            <div class="table-body">
              <div v-for="categoria in categorias" :key="categoria.id" class="table-row">
                <div class="td">
                  <div class="categoria-nome">{{ categoria.nome }}</div>
                </div>
                <div class="td">
                  <span class="tipo-badge" :class="categoria.tipo.toLowerCase()">
                    {{ categoria.tipo }}
                  </span>
                </div>
                <div class="td">
                  <span class="status-badge" :class="categoria.ativa ? 'ativa' : 'inativa'">
                    {{ categoria.ativa ? 'Ativa' : 'Inativa' }}
                  </span>
                </div>
                <div class="td">
                  <button class="action-btn edit" @click="abrirModalCategoria(categoria)">
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                      <path d="M11 4H4C3.46957 4 2.96086 4.21071 2.58579 4.58579C2.21071 4.96086 2 5.46957 2 6V20C2 20.5304 2.21071 21.0391 2.58579 21.4142C2.96086 21.7893 3.46957 22 4 22H18C18.5304 22 19.0391 21.7893 19.4142 21.4142C19.7893 21.0391 20 20.5304 20 20V13" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                      <path d="M18.5 2.49998C18.8978 2.10216 19.4374 1.87866 20 1.87866C20.5626 1.87866 21.1022 2.10216 21.5 2.49998C21.8978 2.89781 22.1213 3.43737 22.1213 3.99998C22.1213 4.56259 21.8978 5.10216 21.5 5.49998L12 15L8 16L9 12L18.5 2.49998Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    </svg>
                  </button>
                  <button class="action-btn delete" @click="confirmarExclusao(categoria)">
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                      <polyline points="3,6 5,6 21,6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                      <path d="M19 6V20C19 20.5304 18.7893 21.0391 18.4142 21.4142C18.0391 21.7893 17.5304 22 17 22H7C6.46957 22 5.96086 21.7893 5.58579 21.4142C5.21071 21.0391 5 20.5304 5 20V6M8 6V4C8 3.46957 8.21071 2.96086 8.58579 2.58579C8.96086 2.21071 9.46957 2 10 2H14C14.5304 2 15.0391 2.21071 15.4142 2.58579C15.7893 2.96086 16 3.46957 16 4V6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    </svg>
                  </button>
                </div>
              </div>
            </div>
          </div>

          <!-- Versão Mobile - Lista Compacta -->
          <div class="mobile-list">
            <div v-for="categoria in categorias" :key="categoria.id" class="table-row">
              <div class="transaction-compact">
                <div class="compact-main">
                  <div class="compact-info">
                    <span class="compact-description">{{ categoria.nome }}</span>
                    <span class="compact-date">{{ categoria.tipo }}</span>
                  </div>
                  <div class="compact-right">
                    <span class="compact-status" :class="categoria.ativa ? 'ativa' : 'inativa'">
                      {{ categoria.ativa ? 'Ativa' : 'Inativa' }}
                    </span>
                  </div>
                </div>
              </div>

              <div class="mobile-actions">
                <button class="action-btn edit" @click="abrirModalCategoria(categoria)">
                  <svg width="14" height="14" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <path d="M11 4H4C3.46957 4 2.96086 4.21071 2.58579 4.58579C2.21071 4.96086 2 5.46957 2 6V20C2 20.5304 2.21071 21.0391 2.58579 21.4142C2.96086 21.7893 3.46957 22 4 22H18C18.5304 22 19.0391 21.7893 19.4142 21.4142C19.7893 21.0391 20 20.5304 20 20V13" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    <path d="M18.5 2.49998C18.8978 2.10216 19.4374 1.87866 20 1.87866C20.5626 1.87866 21.1022 2.10216 21.5 2.49998C21.8978 2.89781 22.1213 3.43737 22.1213 3.99998C22.1213 4.56259 21.8978 5.10216 21.5 5.49998L12 15L8 16L9 12L18.5 2.49998Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                  Editar
                </button>
                <button class="action-btn delete" @click="confirmarExclusao(categoria)">
                  <svg width="14" height="14" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                    <polyline points="3,6 5,6 21,6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    <path d="M19 6V20C19 20.5304 18.7893 21.0391 18.4142 21.4142C18.0391 21.7893 17.5304 22 17 22H7C6.46957 22 5.96086 21.7893 5.58579 21.4142C5.21071 21.0391 5 20.5304 5 20V6M8 6V4C8 3.46957 8.21071 2.96086 8.58579 2.58579C8.96086 2.21071 9.46957 2 10 2H14C14.5304 2 15.0391 2.21071 15.4142 2.58579C15.7893 2.96086 16 3.46957 16 4V6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  </svg>
                  Excluir
                </button>
              </div>
            </div>
          </div>
        </div>

        <div v-else class="empty-state">
          <svg width="48" height="48" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M20 7L9 18L4 13" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          <h4>Nenhuma categoria encontrada</h4>
          <p>Comece criando sua primeira categoria</p>
        </div>

        <!-- Paginação -->
        <div v-if="totalPaginas > 1" class="paginacao">
          <button 
            @click="mudarPagina(paginaAtual - 1)" 
            :disabled="paginaAtual === 0"
            class="pagina-btn"
          >
            Anterior
          </button>
          <span class="pagina-info">
            Página {{ paginaAtual + 1 }} de {{ totalPaginas }}
          </span>
          <button 
            @click="mudarPagina(paginaAtual + 1)" 
            :disabled="paginaAtual === totalPaginas - 1"
            class="pagina-btn"
          >
            Próxima
          </button>
        </div>
      </div>
    </div>

    <!-- Modal de Categoria -->
    <div v-if="mostrarModal" class="modal-overlay" @click="fecharModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>{{ categoriaEditando ? 'Editar Categoria' : 'Nova Categoria' }}</h3>
          <button @click="fecharModal" class="close-modal">×</button>
        </div>
        <form @submit.prevent="salvarCategoria" class="modal-form">
          <div class="form-group">
            <label>Nome da Categoria *</label>
            <input type="text" v-model="formCategoria.nome" required class="form-input" placeholder="Ex: Alimentação, Transporte, Salário..." />
          </div>
          <div class="form-group">
            <label>Tipo *</label>
            <select v-model="formCategoria.tipo" required class="form-input">
              <option value="">Selecione o tipo</option>
              <option value="RECEITA">Receita</option>
              <option value="DESPESA">Despesa</option>
            </select>
          </div>
          <div class="modal-actions">
            <button type="button" @click="fecharModal" class="btn-cancel">Cancelar</button>
            <button type="submit" :disabled="carregando" class="btn-save">
              <span v-if="carregando" class="loading-spinner"></span>
              {{ carregando ? 'Salvando...' : 'Salvar' }}
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- Modal de Confirmação de Exclusão -->
    <div v-if="categoriaParaExcluir" class="modal-overlay" @click="categoriaParaExcluir = null">
      <div class="modal-content confirmation-modal" @click.stop>
        <div class="modal-header">
          <h3>Confirmar Exclusão</h3>
          <button @click="categoriaParaExcluir = null" class="close-modal">×</button>
        </div>
        <div class="confirmation-content">
          <div class="confirmation-icon">
            <svg width="48" height="48" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="2"/>
              <line x1="15" y1="9" x2="9" y2="15" stroke="currentColor" stroke-width="2"/>
              <line x1="9" y1="9" x2="15" y2="15" stroke="currentColor" stroke-width="2"/>
            </svg>
          </div>
          <h4>Tem certeza que deseja excluir esta categoria?</h4>
          <div class="categoria-info">
            <p><strong>{{ categoriaParaExcluir.nome }}</strong></p>
            <p>Tipo: <span class="tipo-badge" :class="categoriaParaExcluir.tipo.toLowerCase()">{{ categoriaParaExcluir.tipo }}</span></p>
            <p class="warning-text">⚠️ Esta ação não pode ser desfeita!</p>
          </div>
          <div class="confirmation-actions">
            <button @click="categoriaParaExcluir = null" class="btn-cancel">Cancelar</button>
            <button @click="excluirCategoria" :disabled="carregando" class="btn-delete">
              <span v-if="carregando" class="loading-spinner"></span>
              {{ carregando ? 'Excluindo...' : 'Excluir' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import apiService from '@/services/apiService';

export default {
  name: 'CategoriasView',
  data() {
    return {
      mostrarFiltros: false,
      
      filtroNome: '',
      filtroTipo: '',
      filtroAtiva: 'true',
      
      carregando: false,
      erro: '',
      sucesso: '',
      
      categorias: [],
      
      paginaAtual: 0,
      tamanhoPagina: 20,
      totalCategorias: 0,
      totalPaginas: 0,
      
      mostrarModal: false,
      categoriaEditando: null,
      formCategoria: {
        nome: '',
        tipo: ''
      },
      categoriaParaExcluir: null,
    };
  },
  methods: {
    toggleFiltros() {
      this.mostrarFiltros = !this.mostrarFiltros;
    },

    async carregarCategorias() {
      this.carregando = true;
      this.erro = '';
      
      try {
        const params = {
          pageNumber: this.paginaAtual,
          pageSize: this.tamanhoPagina
        };
        
        if (this.filtroNome) params.nomeCategoria = this.filtroNome;
        if (this.filtroTipo) params.tipo = this.filtroTipo;
        if (this.filtroAtiva !== '') params.ativa = this.filtroAtiva;
        
        const data = await apiService.getCategorias(params);
        this.categorias = data.content;
        this.totalCategorias = data.totalElements;
        this.totalPaginas = data.totalPages;
      } catch (error) {
        console.error('Erro ao carregar categorias:', error);
        this.erro = 'Erro ao carregar categorias. Tente novamente.';
      } finally {
        this.carregando = false;
      }
    },

    mudarPagina(novaPagina) {
      this.paginaAtual = novaPagina;
      this.carregarCategorias();
    },

    abrirModalCategoria(categoria = null) {
      this.categoriaEditando = categoria;
      if (categoria) {
        this.formCategoria = {
          nome: categoria.nome,
          tipo: categoria.tipo
        };
      } else {
        this.formCategoria = {
          nome: '',
          tipo: ''
        };
      }
      this.mostrarModal = true;
    },

    fecharModal() {
      this.mostrarModal = false;
      this.categoriaEditando = null;
    },

    async salvarCategoria() {
      this.carregando = true;
      this.erro = '';
      
      try {
        if (this.categoriaEditando) {
          await apiService.updateCategoria(this.categoriaEditando.id, this.formCategoria);
          this.sucesso = 'Categoria atualizada com sucesso!';
        } else {
          await apiService.createCategoria(this.formCategoria);
          this.sucesso = 'Categoria criada com sucesso!';
        }

        this.fecharModal();
        await this.carregarCategorias();
      } catch (error) {
        console.error('Erro ao salvar categoria:', error);
        this.erro = 'Erro ao salvar categoria. Tente novamente.';
      } finally {
        this.carregando = false;
      }
    },

    confirmarExclusao(categoria) {
      this.categoriaParaExcluir = categoria;
    },

    async excluirCategoria() {
      this.carregando = true;
      try {
        await apiService.deleteCategoria(this.categoriaParaExcluir.id);
        this.sucesso = 'Categoria excluída com sucesso!';
        this.categoriaParaExcluir = null;
        await this.carregarCategorias();
      } catch (error) {
        console.error('Erro ao excluir categoria:', error);
        
        if (error.status === 409) {
          this.erro = error.message || `Não é possível excluir a categoria "${this.categoriaParaExcluir.nome}" pois ela possui transações vinculadas.`;
        } else if (error.status === 400) {
          this.erro = error.message || `Erro de validação ao excluir a categoria "${this.categoriaParaExcluir.nome}".`;
        } else if (error.status === 500) {
          this.erro = error.message || `Erro interno do servidor ao excluir a categoria "${this.categoriaParaExcluir.nome}".`;
        } else {
          this.erro = error.message || 'Erro ao excluir categoria. Tente novamente.';
        }
      } finally {
        this.carregando = false;
      }
    },

    voltarDashboard() {
      this.$router.push('/dashboard');
    }
  },
  
  mounted() {
    this.carregarCategorias();
  }
};
</script>

<style scoped>
* {
  box-sizing: border-box;
}

.dashboard-wrapper {
  min-height: 100vh;
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  position: relative;
}

.background-pattern {
  position: absolute;
  inset: 0;
  background-image: radial-gradient(circle at 1px 1px, rgba(148, 163, 184, 0.1) 1px, transparent 0);
  background-size: 40px 40px;
  opacity: 0.3;
}

.dashboard-header {
  background: rgba(255, 255, 255, 0.95);
  border-bottom: 1px solid rgba(226, 232, 240, 0.8);
  backdrop-filter: blur(10px);
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 12px 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #059669;
  font-weight: 600;
  font-size: 16px;
}

.logo span {
  display: none;
}

.user-menu {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 12px;
  background: transparent;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  color: #64748b;
  cursor: pointer;
  transition: all 0.2s ease;
  font-size: 13px;
}

.user-menu:hover {
  background: #f1f5f9;
  color: #374151;
}

.dashboard-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 16px;
  position: relative;
}

.dashboard-header-section {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 24px;
}

.dashboard-title {
  color: #1e293b;
  font-size: 24px;
  font-weight: 700;
  margin: 0;
  text-align: center;
}

.filter-toggle-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 12px 20px;
  background: rgba(255, 255, 255, 0.95);
  border: 1px solid rgba(226, 232, 240, 0.8);
  border-radius: 8px;
  color: #374151;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 14px;
  font-weight: 500;
  backdrop-filter: blur(10px);
  min-height: 48px;
}

.filter-toggle-btn:hover {
  background: rgba(249, 250, 251, 0.95);
  border-color: #059669;
  color: #059669;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(5, 150, 105, 0.15);
}

.filter-toggle-btn.active {
  background: linear-gradient(135deg, #059669 0%, #10b981 100%);
  color: white;
  border-color: #059669;
}

.filter-toggle-btn .chevron {
  transition: transform 0.3s ease;
}

.filter-toggle-btn .chevron.rotated {
  transform: rotate(180deg);
}

.filter-slide-enter-active,
.filter-slide-leave-active {
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  transform-origin: top;
}

.filter-slide-enter-from {
  opacity: 0;
  transform: translateY(-20px) scaleY(0.8);
}

.filter-slide-leave-to {
  opacity: 0;
  transform: translateY(-20px) scaleY(0.8);
}

.filtros-card {
  background: rgba(255, 255, 255, 0.95);
  border: 1px solid rgba(226, 232, 240, 0.8);
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 24px;
  backdrop-filter: blur(10px);
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
}

.filtros-title {
  color: #374151;
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 12px;
}

.filtros-content {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.filtro-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.filtro-group label {
  color: #374151;
  font-size: 14px;
  font-weight: 500;
}

.select-input, .text-input {
  padding: 12px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  background: white;
  color: #1f2937;
  font-size: 16px;
  transition: all 0.2s ease;
  -webkit-appearance: none;
  appearance: none;
}

.select-input:focus, .text-input:focus {
  outline: none;
  border-color: #059669;
  box-shadow: 0 0 0 3px rgba(5, 150, 105, 0.1);
}

.filtrar-button {
  padding: 12px 20px;
  background: linear-gradient(135deg, #059669 0%, #10b981 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-size: 16px;
  min-height: 48px;
}

.filtrar-button:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(5, 150, 105, 0.3);
}

.filtrar-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

.transacoes-card {
  background: rgba(255, 255, 255, 0.95);
  border: 1px solid rgba(226, 232, 240, 0.8);
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 24px;
  backdrop-filter: blur(10px);
}

.transacoes-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.transacoes-header h3 {
  color: #374151;
  font-size: 16px;
  font-weight: 600;
  margin: 0;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.add-transaction-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 8px 16px;
  background: linear-gradient(135deg, #059669 0%, #10b981 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  font-size: 14px;
  height: 40px;
  white-space: nowrap;
}

.add-transaction-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(5, 150, 105, 0.3);
}

.transacoes-table {
  border-radius: 8px;
  overflow: hidden;
}

.table-header {
  display: none;
}

.table-body {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.table-row {
  background: white;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  transition: all 0.2s ease;
}

.table-row:hover {
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.transaction-compact {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f3f4f6;
}

.compact-main {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
}

.compact-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
  flex: 1;
}

.compact-description {
  font-weight: 500;
  color: #374151;
  font-size: 16px;
}

.compact-date {
  font-size: 14px;
  color: #6b7280;
}

.compact-right {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4px;
}

.compact-status {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.compact-status.ativa {
  background: rgba(5, 150, 105, 0.1);
  color: #059669;
}

.compact-status.inativa {
  background: rgba(156, 163, 175, 0.1);
  color: #6b7280;
}

.mobile-actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.action-btn {
  flex: 1;
  padding: 12px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  font-size: 14px;
  font-weight: 500;
  min-height: 44px;
  min-width: 100px;
}

.action-btn.edit {
  background: rgba(59, 130, 246, 0.1);
  color: #3b82f6;
}

.action-btn.delete {
  background: rgba(220, 38, 38, 0.1);
  color: #dc2626;
}

.action-btn:hover {
  transform: scale(1.02);
}

.desktop-table {
  display: block;
}

.mobile-list {
  display: none;
}

.empty-state {
  text-align: center;
  padding: 48px 24px;
  color: #9ca3af;
}

.empty-state svg {
  margin-bottom: 16px;
}

.empty-state h4 {
  color: #6b7280;
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 8px;
}

.empty-state p {
  color: #9ca3af;
  font-size: 14px;
}

.paginacao {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 12px;
  margin-top: 24px;
  flex-wrap: wrap;
}

.pagina-btn {
  padding: 10px 16px;
  background: #f3f4f6;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  color: #374151;
  cursor: pointer;
  transition: all 0.2s ease;
  font-size: 14px;
  min-height: 44px;
}

.pagina-btn:hover:not(:disabled) {
  background: #e5e7eb;
}

.pagina-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.pagina-info {
  color: #6b7280;
  font-size: 14px;
  text-align: center;
}

.erro-alert, .sucesso-alert {
  padding: 12px 16px;
  border-radius: 8px;
  margin-bottom: 24px;
  display: flex;
  align-items: center;
  gap: 8px;
  position: relative;
}

.erro-alert {
  background: rgba(239, 68, 68, 0.1);
  border: 1px solid rgba(239, 68, 68, 0.2);
  color: #dc2626;
}

.sucesso-alert {
  background: rgba(34, 197, 94, 0.1);
  border: 1px solid rgba(34, 197, 94, 0.2);
  color: #16a34a;
}

.close-error {
  position: absolute;
  right: 12px;
  background: none;
  border: none;
  color: inherit;
  cursor: pointer;
  font-size: 18px;
  font-weight: bold;
  padding: 0;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: flex-end;
  justify-content: center;
  z-index: 1000;
  padding: 0;
}

.modal-content {
  background: white;
  border-radius: 12px 12px 0 0;
  width: 100%;
  max-height: 90vh;
  overflow-y: auto;
  animation: slideUp 0.3s ease-out;
}

@keyframes slideUp {
  from {
    transform: translateY(100%);
  }
  to {
    transform: translateY(0);
  }
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #e5e7eb;
  position: sticky;
  top: 0;
  background: white;
  z-index: 10;
}

.modal-header h3 {
  color: #1f2937;
  font-size: 18px;
  font-weight: 600;
  margin: 0;
}

.close-modal {
  background: none;
  border: none;
  font-size: 24px;
  color: #6b7280;
  cursor: pointer;
  padding: 0;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
  transition: all 0.2s ease;
}

.close-modal:hover {
  background: #f3f4f6;
  color: #374151;
}

.modal-form {
  padding: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-bottom: 16px;
}

.form-group label {
  color: #374151;
  font-size: 14px;
  font-weight: 500;
}

.form-input {
  padding: 12px;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  background: white;
  color: #1f2937;
  font-size: 16px;
  transition: all 0.2s ease;
  -webkit-appearance: none;
  appearance: none;
}

.form-input:focus {
  outline: none;
  border-color: #059669;
  box-shadow: 0 0 0 3px rgba(5, 150, 105, 0.1);
}

.modal-actions {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid #e5e7eb;
}

.btn-cancel {
  padding: 12px 20px;
  background: #f3f4f6;
  color: #374151;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
  font-size: 16px;
  min-height: 48px;
}

.btn-cancel:hover {
  background: #e5e7eb;
}

.btn-save {
  padding: 12px 20px;
  background: linear-gradient(135deg, #059669 0%, #10b981 100%);
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-size: 16px;
  min-height: 48px;
}

.btn-save:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(5, 150, 105, 0.3);
}

.btn-save:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

.loading-spinner {
  width: 16px;
  height: 16px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top: 2px solid white;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.confirmation-modal {
  max-width: 400px;
  border-radius: 12px;
  margin: auto;
  align-self: center;
}

.confirmation-content {
  padding: 20px;
  text-align: center;
}

.confirmation-icon {
  margin-bottom: 16px;
  color: #dc2626;
}

.confirmation-content h4 {
  color: #374151;
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 16px;
}

.categoria-info {
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 20px;
  text-align: left;
}

.categoria-info p {
  margin: 4px 0;
  color: #374151;
}

.warning-text {
  color: #dc2626;
  font-weight: 500;
  font-size: 14px;
  margin-top: 8px !important;
}

.confirmation-actions {
  display: flex;
  gap: 12px;
}

.confirmation-actions .btn-cancel,
.confirmation-actions .btn-delete {
  flex: 1;
}

.btn-delete {
  padding: 12px 20px;
  background: linear-gradient(135deg, #dc2626 0%, #ef4444 100%);
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-size: 16px;
  min-height: 48px;
}

.btn-delete:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(220, 38, 38, 0.3);
}

.btn-delete:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

@media (max-width: 767px) {
  .transacoes-header {
    flex-direction: column;
    gap: 12px;
    align-items: stretch;
  }

  .transacoes-header h3 {
    text-align: center;
  }

  .header-actions {
    justify-content: center;
  }

  .add-transaction-btn {
    padding: 12px 16px;
    font-size: 16px;
    height: 48px;
    flex: 1;
  }

  .desktop-table {
    display: none;
  }
  
  .mobile-list {
    display: block;
  }

  .mobile-actions {
    flex-direction: column;
  }

  .action-btn {
    min-width: auto;
  }
}

@media (min-width: 768px) {
  .header-content {
    padding: 16px 24px;
  }
  
  .logo {
    font-size: 18px;
    gap: 12px;
  }
  
  .logo span {
    display: inline;
  }
  
  .dashboard-container {
    padding: 32px 24px;
  }
  
  .dashboard-header-section {
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 32px;
  }
  
  .dashboard-title {
    font-size: 32px;
    text-align: left;
  }
  
  .filter-toggle-btn {
    width: auto;
    min-height: auto;
  }
  
  .filtros-card {
    padding: 24px;
    margin-bottom: 32px;
  }
  
  .filtros-content {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 16px;
    align-items: end;
  }
  
  .filtrar-button {
    justify-self: start;
  }
  
  .transacoes-card {
    padding: 24px;
    margin-bottom: 32px;
  }
  
  .transacoes-header h3 {
    font-size: 18px;
  }

  .modal-overlay {
    align-items: center;
    padding: 20px;
  }

  .modal-content {
    border-radius: 12px;
    max-width: 500px;
    width: 100%;
    max-height: 80vh;
  }

  .modal-actions {
    flex-direction: row;
  }
}

@media (min-width: 1024px) {
  .table-header {
    display: grid;
    grid-template-columns: 2fr 1fr 1fr 1fr;
    gap: 16px;
    padding: 16px;
    background: #f8fafc;
    border-bottom: 1px solid #e5e7eb;
    font-weight: 600;
    color: #374151;
    font-size: 14px;
  }

  .th {
    text-align: left;
  }

  .table-body {
    display: block;
  }

  .table-row {
    display: grid;
    grid-template-columns: 2fr 1fr 1fr 1fr;
    gap: 16px;
    padding: 16px;
    border-bottom: 1px solid #f3f4f6;
    align-items: center;
    background: white;
    border-radius: 0;
    box-shadow: none;
    margin-bottom: 0;
  }

  .table-row:hover {
    background: #f9fafb;
    box-shadow: none;
  }

  .td {
    display: block;
    margin-bottom: 0;
    position: static;
  }

  .td:nth-child(4) {
    display: flex;
    gap: 8px;
    margin-top: 0;
    padding-top: 0;
    border-top: none;
    justify-content: flex-start;
  }

  .action-btn {
    flex: none;
    padding: 8px 12px;
    min-height: auto;
    font-size: 12px;
    min-width: auto;
  }

  .transaction-compact {
    display: none;
  }

  .mobile-actions {
    display: none;
  }

  .categoria-nome {
    font-weight: 500;
    color: #374151;
    font-size: 16px;
  }

  .tipo-badge {
    padding: 6px 12px;
    border-radius: 6px;
    font-size: 14px;
    font-weight: 500;
    display: inline-block;
  }

  .tipo-badge.receita {
    background: rgba(5, 150, 105, 0.1);
    color: #059669;
  }

  .tipo-badge.despesa {
    background: rgba(220, 38, 38, 0.1);
    color: #dc2626;
  }

  .status-badge {
    padding: 6px 12px;
    border-radius: 6px;
    font-size: 14px;
    font-weight: 500;
    display: inline-block;
  }

  .status-badge.ativa {
    background: rgba(5, 150, 105, 0.1);
    color: #059669;
  }

  .status-badge.inativa {
    background: rgba(156, 163, 175, 0.1);
    color: #6b7280;
  }
}
</style>