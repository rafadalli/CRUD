import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Scanner;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.text.DecimalFormat;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Main {

    private static CRUD2<Usuario> arqUsuario;
    private static CRUD2<Pergunta> arqPergunta;
    private static CRUD2<Resposta> arqRespostas;
    
    private static ArvoreBMais_ChaveComposta_Int_Int indicePerguntas;
    private static ArvoreBMais_ChaveComposta_Int_Int indicePerguntasRespostas;
    private static ArvoreBMais_ChaveComposta_Int_Int indicePerguntasUsuarios;
    private static ArvoreBMais_ChaveComposta_Int_Int indiceUsuariosRespostas;
    private static ListaInvertida indicePalavrasChave;

    private static Scanner leitor = new Scanner(System.in);
    private static InputStreamReader isr = new InputStreamReader(System.in);    
    private static BufferedReader br = new BufferedReader(isr);

//--------------------------------------------------------------------- metodos

    public String getDateTime() {
      DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
      Date date = new Date();
      return dateFormat.format(date);
    }

    public static void Cadastro() throws Exception{
      String email;
      String nome;
      String senha;
  
      Usuario user = new Usuario();
      System.out.println("\nNOVO USUARIO\n");
          
      email = MyIO.readString("E-mail: ");
  
      if (arqUsuario.read(email) == null) {
        nome = MyIO.readString("Nome: ");
        senha = MyIO.readString("Senha: ");
  
        user.setEmail(email);
        user.setNome(nome);
        user.setSenha(senha);
  
        int op = MyIO.readInt("\nDigite 1 para confirmar o cadastro: ");
  
        if (op == 1) {
          arqUsuario.create(user);
          System.out.println("\nUsuario cadastrado com sucesso!");
        }
        else
          System.out.println("Cadastro cancelado!");
      }
  
      else {
        System.out.println("ERRO: E-mail ja existe!");
      }
  
    }

    static int IDLogado = -1;
  
    public static void Login(){
      String email;
      String senha;
  
      System.out.println("\nACESSO AO SISTEMA");
      
      try{
        email = MyIO.readString("\nEmail: ");
        Usuario user = arqUsuario.read(email);
  
        if(user != null){
          senha = MyIO.readString("\nDigite sua senha: ");
  
          if(senha.equals(user.getSenha())){
  
            System.out.println("Entrando");
            MenuLogado();
          
          } else {
  
            System.out.println("Senha Inválida!");
            System.out.println("\nPressione enter para voltar para tela inicial");
            br.read();
  
          }
        } else {
          
          System.out.println("Email não cadastrado.");
          System.out.println("\nPressione enter para voltar para tela inicial");
          br.read();
                  
        }
      } catch(Exception e){
        e.printStackTrace();
      }
    }
    
    static int notificacoes = 0;

    public static void MenuCriacao(){
      
      int opcao;

      System.out.println("\nPERGUNTAS 1.0\n=============");
          System.out.println("FEITO PELO GRUPO 07");
          System.out.println("\nINICIO > CRIAÇÃO DE PERGUNTAS\n");
          System.out.println("1) Listar");
          System.out.println("2) Incluir");
          System.out.println("3) Alterar");
          System.out.println("4) Arquivar");
          System.out.println("\n0) Sair");
          System.out.print("\nOpção: ");
  
          opcao = MyIO.readInt();
  
          if (opcao == 1) {
            Listar();
          } else if (opcao == 2) {
            Incluir();
          } else if (opcao == 3) {
            Alterar();
          } else if (opcao == 4) {
            Arquivar();
          } else {
            System.out.println("ERRO: OPÇÃO INVÁLIDA!");
          }
  
    }

    public static void MenuLogado(){
        
      int opcao;
      do {
  
        System.out.println("\nPERGUNTAS 1.0\n=============");
        System.out.println("FEITO PELO GRUPO 07");
        System.out.println("\nINICIO\n");
        System.out.println("1) Criação de perguntas");
        System.out.println("2) Consultar/responder perguntas");
        System.out.println("3) Notificações: 0");
        System.out.println("\n0) Sair");
        System.out.print("\nOpção: ");
  
        opcao = MyIO.readInt();
        if (opcao == 1) {
          MenuCriacao();
        }

        else if (opcao == 2) {
          try {
            MenuConsultar();
          } catch (Exception e) {
            System.out.println("ERRO: Problema durante o cadastro!");
          }
          opcao = 3;
        }
  
        else if (opcao == 0) {
          opcao = 3;
        }
  
        else {
          System.out.println("\nERRO: Opcao invalida");
        }
  
      } while (opcao < 0 || opcao > 3);
    }

    public static void Listar(){
        
        System.out.println("\n LISTAGEM \n");
        try {
          Pergunta per;
          //int id = MyIO.readInt("\nQual id do usuario?: ");
      
          ArvoreBMais_ChaveComposta_Int_Int arvore = new ArvoreBMais_ChaveComposta_Int_Int(3, "pergunta.db");
      
          int[] lista = arvore.read(IDLogado);
      
          for (int i = 0; i < lista.length; i++) {
            per = arqPergunta.read(lista[i]);
            System.out.println("" + i + ":");
            System.out.println(per.data);
            System.out.println(per.pergunta);
          }

            System.out.println("\nPressione enter para voltar para tela inicial...");
            System.in.read();
            MenuLogado();
        
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } 
    }

    public static void Incluir(){
        
        long time = System.currentTimeMillis(); 
        //String time = getDateTime();
        
        System.out.println("\n INCLUSÃO \n");
        System.out.print("\nPergunta: ");

        try {    
            String pergunta = br.readLine(); 
            Pergunta per = new Pergunta(); 

            System.out.print("\nDigite as palavras chave da sua pergunta, separadas por (;): "); 
            String pc = Biblioteca.toKeyWord(br.readLine());

            if (!pergunta.equals("")) { // se a pergunta não estiver vazia
                System.out.println("\nPergunta Válida!\n");
                per.setPergunta(pergunta); // define o atributo pergunta
                per.setPalavrasChave(pc); 
                System.out.println("Pergunta: " + pergunta);
                System.out.println("Palavras Chaves: " + pc);

                int op = -1;
                while (op != 1 && op != 2) {
                    System.out.println("\nConfirmar pergunta?");

                    System.out.println("\n1) Sim\n2) Não");
                    System.out.print("\nOperação: ");
                    op = leitor.nextInt();
                    if (op == 1){
                        per.setNota((short)0);
                        per.setCriacao(time);
                        per.setIDusuario(IDLogado);

                        String[] keyWords = pc.split(";"); // separa as palavras 
                    
                        int idPergunta = arqPergunta.create(per); 
                        indicePerguntas.create(IDLogado, idPergunta);

                        for (int i = 0; i < keyWords.length; i++){
                            indicePalavrasChave.create(keyWords[i], idPergunta); 
                        }

                    } else if (op == 2){
                        System.out.println("Operação Cancelada.");

                        System.out.println("\nPressione enter para voltar para tela inicial...");
                        System.in.read();
                        MenuLogado();
                    } else System.out.println("Opção Inválida!");
                }
            } 
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public static void Alterar() {

      System.out.println("\n ALTERAR \n");
      try{
          
          int[] indices = indicePerguntas.read(IDLogado); 
          Pergunta p;

          if (indices.length == 0){ 
              System.out.println("Nenhuma pergunta cadastrada!");

          } else {
              
              // escreve as perguntas na tela 
              for (int i = 0, j = 1; i < indices.length; i++){
                  p = arqPergunta.read(indices[i]);

                  if (p.getAtiva() == true){
                      System.out.print("\nQUESTÃO N." + j);
                      System.out.println(p);
                      j++;
                  }
              }
              String aux = "";
              int op = -1;
              
              System.out.println("\n0) Voltar");
              System.out.print("\nInforme o número da questão que deseja modificar: ");

              op = leitor.nextInt() - 1;

              while(op < -1 && op > indices.length){

                  System.out.print("ERRO: Pergunta inexistente. Por favor, informe uma operação válida: ");
                  op = leitor.nextInt() - 1;
              }

              if (op != -1){
                  System.out.println("\nPergunta que deseja modificar:");

                  p = arqPergunta.read(indices[op]); 

                  if(p.getAtiva() == false){
                      op++;
                      p = arqPergunta.read(indices[op]);
                      while(p.getAtiva() == false){
                        op++;
                        p = arqPergunta.read(indices[op]);
                      }
                  }
                  System.out.println(p);

                  System.out.print("\nNOVA PERGUNTA: ");
                  String novaPergunta = br.readLine();

                  if (novaPergunta.equals("")){ // se o usuario digitou algo
                      System.out.println("ERRO! Pergunta vazia.");

                  } else { // confirmação da alteração da pergunta
                      
                      System.out.println("\nDeseja alterar as palavras chave também?");
                      System.out.println("\n1) Sim\n2) Não");
                      System.out.print("Operação: ");
                      int cf = leitor.nextInt();

                      while (cf != 1 && cf != 2){
                          
                          System.out.println("ERRO! Operação Inválida!");
                          
                          System.out.println("\nDeseja alterar as palavras chave também?");
                          System.out.println("\n1) Sim\n2) Não");
                      
                          cf = leitor.nextInt();
                      }

                      if (cf == 1){

                          System.out.print("\nDigite as palavras chave da nova pergunta, separadas por (;): "); 
                          aux = Biblioteca.toKeyWord(br.readLine());

                          while (aux == ""){
                              System.out.println("ERRO! Nenhuma palavra escrita!");

                              System.out.print("\nDigite as palavras chave da nova pergunta, separadas por (;): "); 
                              aux = Biblioteca.toKeyWord(br.readLine());
                          }

                          System.out.println("\nDeseja confirmar a atualização da pergunta e das palavras chave?");
                          System.out.println("\nPergunta alterada: " + novaPergunta);
                          System.out.println("Palavras chave atualizadas: " + aux);
                          System.out.println("\n1) Sim\n2) Não");
                          System.out.print("\nOperação: ");

                          cf = leitor.nextInt();
                          
                          while(cf != 1 && cf != 2){
                              System.out.print("ERRO: Operação inválida! Você deseja confirmar a atualização? ");
                              cf = leitor.nextInt();
                          }
                          
                          if (cf == 1){
                              indicePalavrasChave.print();
                              //palavras chave anteriores
                              String[] palavrasChaves = p.getPalavrasChave().split(";"); 

                              for (int i = 0; i < palavrasChaves.length; i++){
                                indicePalavrasChave.delete(palavrasChaves[i], p.getID()); 
                              }
                              
                              //palavras chave atuais
                              String[] keyWords = aux.split(";");
                              for (int i = 0; i < keyWords.length; i++){
                                indicePalavrasChave.create(keyWords[i], p.getID()); 
                              }

                              p.setPergunta(novaPergunta); 
                              p.setPalavrasChave(aux); 
                              arqPergunta.update(p); 

                              if(arqPergunta.update(p)){
                                  System.out.println("Alteração realizada com sucesso!");
                              }else{
                                System.out.println("Erro na alteração!");
                              }

                          } else if (cf == 2){
                              System.out.println("Operação Cancelada.");
                          }
                          indicePalavrasChave.print();
                      } else {
                          
                          System.out.println("\nDeseja confirmar a alteração da pergunta?");
                          System.out.println("\nPergunta alterada: " + novaPergunta);
                          System.out.println("\n1) Sim\n2) Não");
                          System.out.print("\nOperação: ");

                          cf = leitor.nextInt();

                          while(cf != 1 && cf != 2){
                              System.out.print("ERRO: Operação inválida! Você deseja confirmar a atualização? ");
                              cf = leitor.nextInt();
                          }

                          if (cf == 1){
                              p.setPergunta(novaPergunta); // muda a pergunta
                              arqPergunta.update(p); // atualiza o arquivo
                              
                              if(arqPergunta.update(p)){
                                  System.out.println("Alteração realizada com sucesso!");
                              }else{
                                System.out.println("Erro na alteração!");
                              }
                          } else{
                              System.out.println("Operação Cancelada.");
                          }
                      }
                  }
              }
          }

          System.out.println("\nPressione enter para voltar para tela inicial...");
          System.in.read();
          MenuLogado();

      } catch(Exception e){
          System.out.println(e.getMessage());
      }
  }

    public static void Arquivar(){
        
        System.out.println("\n ARQUIVAR \n");

        try {
            int[] indices = indicePerguntas.read(IDLogado); 

            if (indices.length == 0){ 
                System.out.println("Nenhuma pergunta cadastrada!");

            } else {

                //escreve as perguntas na tela
                for (int i = 0, j = 1; i < indices.length; i++){
                    Pergunta p = arqPergunta.read(indices[i]);

                    if (p.getAtiva() == true){
                        System.out.print("\nQUESTÃO N." + j);
                        System.out.println(p);
                        j++;
                    }
                }

                int op = -1; 
                
                System.out.println("\n0) Voltar");
                System.out.print("\nInforme o número da questão que deseja arquivar: ");

                op = leitor.nextInt() - 1;

                while(op < -1 || op > indices.length){

                    System.out.print("ERRO: Pergunta inexistente. Por favor, informe uma operação válida: ");
                    op = leitor.nextInt() - 1;
                }

                if (op != -1){
                    System.out.println("\nPergunta que deseja arquivar:");
                    
                    // escreve a pergunta que o usuário escolheu
                    Pergunta p = arqPergunta.read(indices[op]);
                    System.out.println(p);

                    int cf = -1;

                    System.out.println("\nConfirmar arquivamento da pergunta.");

                    System.out.println("\n1) Sim\n2) Não");
                    System.out.print("Operação: ");
                    cf = leitor.nextInt();

                    if (cf == 1){
                        p.setAtiva(false);
                        arqPergunta.update(p); 
                        
                        String[] palavrasChaves = p.getPalavrasChave().split(";");

                        for (int i = 0; i < palavrasChaves.length; i++){
                            indicePalavrasChave.delete(palavrasChaves[i], p.getID());
                        }

                        if (arqPergunta.update(p) == true){
                            System.out.println("Pergunta arquivada!");
                        } else System.out.println("Erro ao arquivar.");

                    } else if (cf == 2){
                        System.out.println("Operação Cancelada.");
                    } else {
                        System.out.println("ERRO! Operação inválida!");
                    }
                }
            }

            System.out.println("\nPressione enter para voltar para tela inicial...");
            System.in.read();
            MenuLogado();

        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

//Menu de consulta
    
    public static void MenuConsultar(){
        
        System.out.println("\n        INICIO > PERGUNTAS      ");
        System.out.println("\nColoque as palavras chaves separadas por ;");

        System.out.print("\nPalavras Chave: ");

        try {
            String[] busca = Biblioteca.toKeyWord(br.readLine()).split(";");

            int[] respostas = indicePalavrasChave.read(busca[0]);

            for (int i = 0; i < busca.length; i++){
                int[] aux = indicePalavrasChave.read(busca[i]);
                respostas = Biblioteca.intersecao(respostas, aux);
            }

            Pergunta perguntas[] = new Pergunta[respostas.length];

            for (int i = 0; i < perguntas.length; i++){
                perguntas[i] = arqPergunta.read(respostas[i]);
            }

            perguntas = Biblioteca.insercao(perguntas.length, perguntas);

            if (perguntas.length > 0){
                System.out.println("\n\nPERGUNTAS ENCONTRADAS");
                for (int i = 0, j = 1; i < perguntas.length; i++,j++){
                    System.out.print("\nQUESTÃO N." + j);
                    System.out.println(perguntas[i]);
                }

                System.out.println("\n0) Voltar");
                System.out.print("\nSelecione uma pergunta: ");

                int op = leitor.nextInt() - 1;

                while (op < -1 || op > perguntas.length){
                    System.out.println("ERRO! Operação Inválida!");

                    System.out.print("\nSelecione uma pergunta: ");
                    op = br.read() - 1;
                }

                Pergunta p = perguntas[op];

                Usuario user = arqUsuario.read(p.getIDusuario());

                if (op!= -1){
                    
                    System.out.println("\n        INICIO > PERGUNTAS      ");
                    System.out.println("\n\n" + p.getPergunta().toUpperCase() + 
                    "\n-----------------------------------------");
                    System.out.println("Criada em " + p.getCriacao() + " por " + user.getNome() + ".");
                    System.out.println("Palavras Chaves: " + p.getPalavrasChave());
                    System.out.println("Nota: " + p.getNota() + "\n");

                    System.out.println("COMENTARIOS\n-------------------------\n\n\n\n\n");
                    System.out.println("RESPOSTAS\n-------------------------\n\n\n\n\n");

                    System.out.println("1) Responder\n2) Comentar\n3) Avaliar\n\n0) Voltar");

                    System.out.print("\nOperação: ");
                    op = MyIO.readInt();

                    switch (op) {
                    case 0:
                        break;

                    case 1:
                        MenuResposta();
                        break;

                    default:
                        System.out.println("\nERRO: Opção Inválida!\n");
                }

                }
            } else {
                System.out.println("\nNenhuma pergunta encontrada!");
            }

        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public static int idPergunta = -1;

    // public static int listarRespostas(int[] arrayIdRespostas) throws Exception{
    //     int cont = 0;

    //     System.out.println("\t\nListando:\n");

    //     for(int i = 0; i < arrayIdRespostas.length; i++){
    //         Resposta temp = arqRespostas.read(arrayIdRespostas[i]);
    //         System.out.print("\n\n"+(i+1)+". ");
    //         System.out.println(temp);


    //         cont++;
    //     }

    //     return cont;
    // }

    public static void ListarRespostas() {

        try {
            System.out.println("\n\n LISTAR \n");

            int indices[] = indicePerguntasRespostas.read(idPergunta);
            Resposta respostasUsuario[] = new Resposta[indices.length];

            if (indices.length == 0) { // caso não tenha nenhum ID registrado
                System.out.println("Nenhuma resposta cadastrada!");

            } else {

                for (int i = 0, j = 0; i < indices.length; i++) {

                    Resposta r = arqRespostas.read(indices[i]);

                    if (r.getIDUsuario() == IDLogado) {
                        respostasUsuario[j] = r;
                        j++;
                    }
                }

                // pra cada ID gera uma pergunta
                for (int i = 0; i < respostasUsuario.length; i++) {

                    Usuario user = arqUsuario.read(IDLogado);

                    if (respostasUsuario[i].getAtiva() == false) { // Se ela estiver arquivada
                        System.out.print("\n-----| ARQUIVADA |-----");
                    }

                    if (respostasUsuario[i] != null) {
                        // Escreve a resposta na tela
                        System.out.print("\nN." + (i + 1) + "\n" + respostasUsuario[i].getResposta());
                        System.out.println("\nRespondida em " + respostasUsuario[i].getCriacao() + " por "
                                + user.getNome() + "\nNota: " + respostasUsuario[i].getNota());
                    }
                }
            }

            System.out.print("\nPressione enter para voltar para menu anterior...");
            br.read();
            MenuResposta();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void IncluirResposta() {

        long time = System.currentTimeMillis(); // Hora de criação da pergunta

        try {
            System.out.println("\n\n INCLUSÃO ");
            System.out.print("\nResposta: ");
            String resposta = br.readLine();

            if (!resposta.equals("")) {

                int op = -1;
                while (op != 1 && op != 2) {

                    System.out.println("\nConfirmar resposta?");

                    System.out.println(resposta);
                    System.out.println("\n1) Sim\n2) Não");

                    System.out.print("\nOperação: ");
                    op = leitor.nextByte();

                    if (op == 1) {
                        Resposta resp = new Resposta();
                        resp.setResposta(resposta);

                        resp.setNota((short) 0); // define o atributo nota
                        resp.setCriacao(time); // define o atributo criação
                        resp.setIDUsuario(IDLogado); // define o atributo id Usuario
                        resp.setIDPergunta(idPergunta); // define o atributo ID Pergunta

                        int id = arqRespostas.create(resp);

                        indicePerguntasRespostas.create(idPergunta, id);
                        indiceUsuariosRespostas.create(IDLogado, id);

                        System.out.println("\nRESPOSTA INCLUIDA!");

                    } else if (op == 2) {
                        System.out.println("\nInclusão cancelada!");
                    } else
                        System.out.println("\nOperação Inválida!");
                }
            } else {
                System.out.println("\nPergunta vazia!");
            }

            System.out.print("\nPressione enter para voltar para menu anterior...");
            br.read();
            MenuResposta();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void AlterarResposta() {

        try {
            System.out.println("\n\n ALTERAR \n");

            int indices[] = indicePerguntasRespostas.read(idPergunta);

            if (indices.length == 0) { // caso não tenha nenhum ID registrado
                System.out.println("Nenhuma resposta cadastrada!");

            } else {

                Resposta respostasUsuario[] = new Resposta[indices.length];

                for (int i = 0, j = 0; i < indices.length; i++) {

                    Resposta r = arqRespostas.read(indices[i]);
                    if (r.getIDUsuario() == IDLogado && r.getAtiva()) {
                        respostasUsuario[j] = r;
                        j++;
                    }
                }

                // pra cada ID gera uma pergunta
                for (int i = 0; i < respostasUsuario.length; i++) {
                    Resposta r = respostasUsuario[i];
                    Usuario user = arqUsuario.read(IDLogado);

                    // Escreve a pergunta na tela
                    System.out.print("N." + (i + 1) + "\n" + r.getResposta());
                    System.out.println(
                            "\nRespondida em " + r.getCriacao() + " por " + user.getNome() + "\nNota: " + r.getNota());

                }

                System.out.println("\n0) Voltar");
                System.out.print("\nSelecione uma resposta: ");

                int op = leitor.nextInt() - 1;

                while (op < -1 || op >= indices.length) {
                    System.out.println("ERRO! Resposta inexistente! Por favor, informe uma opção válida.");

                    System.out.print("\nSelecione uma pergunta: ");
                    op = leitor.nextByte() - 1;
                }

                if (op != -1) {

                    Resposta resp = respostasUsuario[op];

                    System.out.println(resp);
                    System.out.print("\nNova resposta: ");

                    String novaResposta = br.readLine();

                    if (!novaResposta.equals("")) {

                        System.out.println("Confirmar alteração?");
                        System.out.println("\n1) Sim\n2) Não");

                        System.out.print("Operação: ");
                        op = leitor.nextByte();

                        if (op == 1) {

                            resp.setResposta(novaResposta);
                            if (arqRespostas.update(resp) == true) {
                                System.out.println("\nRESPOSTA ALTERADA!");
                            } else
                                System.out.println("ERRO! Resposta não foi alterada");

                        } else if (op == 2) {
                            System.out.println("Alteração cancelada!");

                        } else
                            System.out.println("ERRO! Valor inválido");

                    } else {
                        System.out.println("ERRO! Resposta vazia!");
                    }
                }
            }

            System.out.print("\nPressione enter para voltar para menu anterior...");
            br.read();
            MenuResposta();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void ArquivarResposta() {

        try {
            System.out.println("\n\n ARQUIVAR \n");

            int indices[] = indicePerguntasRespostas.read(idPergunta);

            if (indices.length == 0) { // caso não tenha nenhum ID registrado
                System.out.println("Nenhuma resposta cadastrada!");

            } else {

                Resposta respostasUsuario[] = new Resposta[indices.length];

                for (int i = 0, j = 0; i < indices.length; i++) {

                    Resposta r = arqRespostas.read(indices[i]);
                    if (r.getIDUsuario() == IDLogado && r.getAtiva()) {
                        respostasUsuario[j] = r;
                        j++;
                    }
                }

                // pra cada ID gera uma pergunta
                for (int i = 0; i < respostasUsuario.length; i++) {
                    Resposta r = respostasUsuario[i];
                    Usuario user = arqUsuario.read(IDLogado);

                    // Escreve a pergunta na tela
                    System.out.print("\nN." + (i + 1) + "\n" + r.getResposta());
                    System.out.println(
                            "\nRespondida em " + r.getCriacao() + " por " + user.getNome() + "\nNota: " + r.getNota());

                }

                System.out.println("\n0) Voltar");
                System.out.print("\nSelecione uma pergunta: ");

                int op = leitor.nextInt() - 1;

                while (op < -1 || op >= indices.length) {
                    System.out.println("ERRO! Resposta inexistente! Por favor, informe uma opção válida.");

                    System.out.print("\nSelecione uma pergunta: ");
                    op = leitor.nextByte() - 1;
                }

                if (op != -1) {

                    Resposta resp = respostasUsuario[op];

                    System.out.println(resp);

                    System.out.println("Confirmar arquivamento?");
                    System.out.println("\n1) Sim\n2) Não");

                    System.out.print("Operação: ");
                    op = leitor.nextByte();

                    if (op == 1) {

                        resp.setAtiva(false);
                        if (arqRespostas.update(resp) == true) {
                            System.out.println("\nRESPOSTA ARQUIVADA!");

                            indicePerguntasRespostas.delete(idPergunta, resp.getID());
                            indiceUsuariosRespostas.delete(IDLogado, resp.getID());

                        } else System.out.println("ERRO ao arquivar");

                        
                    } else if (op == 2) {
                        System.out.println("Operação Cancelada!");

                    } else System.out.println("ERRO! Valor inválido");
                }
            }

            System.out.print("\nPressione enter para voltar para menu anterior...");
            br.read();
            MenuResposta();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    public static void MenuResposta(){
      int op = 5;
        while (op != 0) {

            System.out.println("\n        INICIO > PERGUNTAS > RESPONDER      ");

            try {
                Pergunta p = arqPergunta.read(idPergunta);
                Usuario user = arqUsuario.read(p.getID());

                System.out.println(
                        "\n\n" + p.getPergunta().toUpperCase() + "\n-----------------------------------------");
                System.out.println("Criada em " + p.getCriacao() + " por " + user.getNome() + ".");
                System.out.println("Palavras Chaves: " + p.getPalavrasChave());
                System.out.println("Nota: " + p.getNota() + "\n");

                System.out.println(
                        "\n1) Listar suas respostas\n2) Incluir nova resposta\n3) Alterar uma resposta\n4) Arquivar uma resposta\n\n0) Retornar ao menu anterior");

                System.out.print("\nOperação: ");
                op = leitor.nextInt();


                if (op == 0) {
                  MenuLogado();
                  
                }

                else if(op == 1){
                  ListarRespostas();
                }

                else if (op == 2) {
                  IncluirResposta();
                }

                else if (op == 3) {
                  AlterarResposta();
                }

                else if (op == 4) {
                  ArquivarResposta();
                }

                else {
                  System.out.println("\nERRO: Opcao invalida");
                }

            } catch (Exception e) {
                MyIO.println("Erro!!");
            }
            
        }
      }

//--------------------------------------------------------------------- main

    public static void main(String[] args) throws Exception{

      try {
          arqUsuario = new CRUD2<>(Usuario.class.getConstructor(), "usuarios.db");
          arqPergunta = new CRUD2<>(Pergunta.class.getConstructor(), "perguntas.db");
          indicePerguntas = new ArvoreBMais_ChaveComposta_Int_Int(10,"perguntas.db");
          arqRespostas = new CRUD2<>(Resposta.class.getConstructor(), "respostas.db");
          indicePalavrasChave = new ListaInvertida(10, "dicionario.db", "blocos.db");
      } catch (Exception e){
        e.printStackTrace();
      }
  
      int opcao;
      do {
  
        System.out.println("\nPERGUNTAS 1.0\n=============");
        System.out.println("\nFEITO PELO GRUPO 07");
        System.out.println("\nACESSO\n");
        System.out.println("1) Acesso ao sistema");
        System.out.println("2) Novo usuário (primeiro acesso)");
        System.out.println("\n0) Sair");
        System.out.print("\nOpcao: ");
  
        opcao = MyIO.readInt();
        if (opcao == 1) {
          Login();
        }
        
        else if (opcao == 2) {
          try {
          Cadastro();
          } catch (Exception e){
            System.out.println ("ERRO: Problema durante o cadastro!");
          }
          opcao = 3;
        }
  
        else if (opcao == 0) {
  
        }
  
        else {
          System.out.println("\nERRO: Opcao invalida");
        }
  
      } while (opcao < 0 || opcao > 2);
    }
}