using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace FakeClient
{
    public partial class Explorer : Form
    {
        public Explorer()
        {
            InitializeComponent();
            foreach (string K in (string[])Program.FTPClient.LastQuery)
                arquivos.Items.Add(K);
        }

        private void arquivos_SelectedIndexChanged(object sender, EventArgs e)
        {
            MessageBox.Show(arquivos.SelectedItem.ToString());
        }
    }
}
