namespace FakeClient
{
    partial class Explorer
    {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            this.arquivos = new System.Windows.Forms.ListBox();
            this.SuspendLayout();
            // 
            // arquivos
            // 
            this.arquivos.FormattingEnabled = true;
            this.arquivos.Location = new System.Drawing.Point(33, 32);
            this.arquivos.Name = "arquivos";
            this.arquivos.Size = new System.Drawing.Size(413, 329);
            this.arquivos.TabIndex = 0;
            this.arquivos.SelectedIndexChanged += new System.EventHandler(this.arquivos_SelectedIndexChanged);
            // 
            // Explorer
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(484, 851);
            this.Controls.Add(this.arquivos);
            this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.FixedSingle;
            this.MaximizeBox = false;
            this.Name = "Explorer";
            this.Text = "Explorer";
            this.ResumeLayout(false);

        }

        #endregion

        private System.Windows.Forms.ListBox arquivos;
    }
}