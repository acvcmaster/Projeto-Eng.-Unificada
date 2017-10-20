using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using FakeClient.Networking;

namespace FakeClient
{
    public partial class ClienteFake : Form
    {
        public ClienteFake()
        {
            InitializeComponent();
        }

        private void ccbtn_Click(object sender, EventArgs e)
        {
            Program.FTPClient = new FTP(serverAddr.Text, "anonymous", "anonymous@domain.com");
            ConsoleWrite($"Conectando-se a : '{serverAddr.Text}'...");
            Program.FTPClient.directoryListSimple("/"); // Listar raíz
            if (Program.FTPClient.LastQuery != null)
                OpenExplorer();
            else
                ConsoleWrite("Falha na conexão.");
        }
        private void OpenExplorer()
        {
            Explorer Explorer = new Explorer();
            Explorer.Location = this.Location;
            Explorer.StartPosition = FormStartPosition.Manual;
            Explorer.FormClosing += delegate { Application.Exit(); };
            Explorer.Show();
            this.Hide();
        }
        private void ConsoleWrite(string s)
        {
            console.Text += s + "\n";
            console.Update();
        }
        private void ClearConsole()
        {
            console.Text = String.Empty;
            console.Update();
        }
    }
}
